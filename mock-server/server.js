const express = require("express");
const fs = require("fs");
const path = require("path");

const app = express();
app.use(express.json());

// ─── DB paths ─────────────────────────────────────────────────────────────────
const ALERTS_DB = path.join(__dirname, "alerts.json");
const TASKS_DB  = path.join(__dirname, "tasks.json");

// ═══════════════════════════════════════════════════════════════════════════════
// TIME CONSTANTS
// ═══════════════════════════════════════════════════════════════════════════════
//
//  APR_14  = 2026-04-14 00:00 UTC  — "today", full dataset
//  APR_15  = 2026-04-15 00:00 UTC  — next-day sync tier
//  APR_16  = 2026-04-16 00:00 UTC  — two-day sync tier
//
//  Sync tiers for /fields/sync:
//    since = null / 0               → APR_14 snapshot (first launch)
//    since < MAR_01 (very old)      → APR_14 snapshot (stale client)
//    since in [MAR_01, APR_14)      → APR_14 snapshot (normal first sync)
//    since in [APR_14, APR_15)      → APR_15 delta (changes on Apr 15)
//    since in [APR_15, APR_16)      → APR_16 delta (changes on Apr 16)
//    since >= APR_16                → empty  (client is up to date)

const APR_14 = 1776124800000; // 2026-04-14 00:00 UTC
const APR_15 = 1776211200000; // 2026-04-15 00:00 UTC
const APR_16 = 1776297600000; // 2026-04-16 00:00 UTC
const MAR_01 = 1772323200000; // 2026-03-01 00:00 UTC  (stale threshold)

// ═══════════════════════════════════════════════════════════════════════════════
// FIELD POINTS  (polygons — static, never change)
// ═══════════════════════════════════════════════════════════════════════════════

let _pointId = 1;
function pts(fieldId, coords) {
  return coords.map(([lat, lng], i) => ({
    id: _pointId++,
    fieldId,
    pointOrder: i,
    latitude: lat,
    longitude: lng,
  }));
}

const FIELD_POINTS = [
  ...pts(101, [ // North Field
    [50.286100, 71.623300], [50.286450, 71.623650], [50.286250, 71.624100],
    [50.285850, 71.624000], [50.285750, 71.623500],
  ]),
  ...pts(102, [ // South Field
    [50.284200, 71.623250], [50.284550, 71.623550], [50.284350, 71.624000],
    [50.283950, 71.623900], [50.283850, 71.623450],
  ]),
  ...pts(103, [ // East Orchard
    [50.285000, 71.624250], [50.285350, 71.624550], [50.285150, 71.624950],
    [50.284800, 71.624900], [50.284700, 71.624450],
  ]),
  ...pts(104, [ // West Greenhouse
    [50.285050, 71.622350], [50.285350, 71.622650], [50.285200, 71.623000],
    [50.284850, 71.622950], [50.284750, 71.622550],
  ]),
  ...pts(105, [ // Archived Field
    [50.286900, 71.624650], [50.287200, 71.624950], [50.287050, 71.625350],
    [50.286700, 71.625250], [50.286600, 71.624850],
  ]),
];

// ═══════════════════════════════════════════════════════════════════════════════
// FIELDS BASE  (id, farmId, title — never change)
// ═══════════════════════════════════════════════════════════════════════════════

const FIELDS_BASE = [
  { id: 101, farmId: 1, title: "North Field",     area: 4.2,  type: "CROP"       },
  { id: 102, farmId: 1, title: "South Field",     area: 3.8,  type: "CROP"       },
  { id: 103, farmId: 1, title: "East Orchard",    area: 2.1,  type: "ORCHARD"    },
  { id: 104, farmId: 1, title: "West Greenhouse", area: 0.6,  type: "GREENHOUSE" },
  { id: 105, farmId: 1, title: "Archived Field",  area: 1.5,  type: "CROP"       },
];

// ─── history helpers ──────────────────────────────────────────────────────────
// halfHourlyHistory: 32 days x 48 points/day (every 30 min), Mar 15 2026 -> Apr 15 2026
// singleDayHistory:  1 day x 48 points, for delta responses (Apr 15 2026 / Apr 16 2026)

function clamp(v, min, max) { return Math.max(min, Math.min(max, v)); }

function makeLcg(seed) {
  let s = seed >>> 0;
  return function(range) {
    s = (s * 1664525 + 1013904223) & 0xffffffff;
    return ((s >>> 0) / 0xffffffff - 0.5) * 2 * range;
  };
}

function luxForSlot(base, slot, next) {
  const hour = slot / 2;
  const daylight = Math.max(0, Math.sin(((hour - 6) / 12) * Math.PI));
  const jitter = next(base * 0.08 + 500);
  return clamp(Math.round(base * daylight + jitter), 0, 120000);
}

function halfHourlyHistory(fieldSeed, base) {
  if (base.soilMoisture === null) return [];
  const entries = [];
  const startMs = 1773532800000; // 2026-03-15 00:00 UTC
  const HALF_H  = 1800000;
  const DAYS    = 32; // 15 Mar – 15 Apr 2026 inclusive (32 days × 48 slots = 1536 records)

  for (let d = 0; d < DAYS; d++) {
    const dayBase = {
      soilMoisture:    clamp(base.soilMoisture    + d * 0.1  + makeLcg(fieldSeed * 31 + d)(3),   5,  95),
      soilTemperature: clamp(base.soilTemperature + d * 0.05 + makeLcg(fieldSeed * 31 + d)(1.5), 2,  40),
      airTemperature:  clamp(base.airTemperature  + d * 0.08 + makeLcg(fieldSeed * 31 + d)(2),   0,  50),
      airHumidity:     clamp(base.airHumidity     - d * 0.05 + makeLcg(fieldSeed * 31 + d)(4),  10,  98),
    };
    for (let slot = 0; slot < 48; slot++) {
      const slotRng = makeLcg(fieldSeed * 10000 + d * 100 + slot);
      entries.push({
        recordedAt:      startMs + d * 86400000 + slot * HALF_H,
        soilMoisture:    clamp(Math.round(dayBase.soilMoisture    + slotRng(2)),   5,  95),
        soilTemperature: clamp(Math.round(dayBase.soilTemperature + slotRng(1)),   2,  40),
        airTemperature:  clamp(Math.round(dayBase.airTemperature  + slotRng(1.5)), 0,  50),
        airHumidity:     clamp(Math.round(dayBase.airHumidity     + slotRng(3)),  10,  98),
        lux:             luxForSlot(base.lux, slot, makeLcg(fieldSeed * 10000 + d * 100 + slot + 99)),
      });
    }
  }
  return entries;
}

function singleDayHistory(fieldSeed, dayStartMs, base) {
  if (base.soilMoisture === null) return [];
  const entries = [];
  const HALF_H = 1800000;
  for (let slot = 0; slot < 48; slot++) {
    const slotRng = makeLcg(fieldSeed * 50000 + dayStartMs + slot);
    entries.push({
      recordedAt:      dayStartMs + slot * HALF_H,
      soilMoisture:    clamp(Math.round(base.soilMoisture    + slotRng(2)),   5,  95),
      soilTemperature: clamp(Math.round(base.soilTemperature + slotRng(1)),   2,  40),
      airTemperature:  clamp(Math.round(base.airTemperature  + slotRng(1.5)), 0,  50),
      airHumidity:     clamp(Math.round(base.airHumidity     + slotRng(3)),  10,  98),
      lux:             luxForSlot(base.lux, slot, makeLcg(fieldSeed * 50000 + dayStartMs + slot + 77)),
    });
  }
  return entries;
}

// ═══════════════════════════════════════════════════════════════════════════════
// SNAPSHOTS
// ═══════════════════════════════════════════════════════════════════════════════
//
// health:       0=HEALTHY  1=WARNING  2=CRITICAL
// connectivity: 0=ONLINE   1=OFFLINE
// lifecycle:    0=ACTIVE   3=ARCHIVED
//
// ┌────┬──────────────────┬──────────┬─────────────┬──────────┬────────────────────────────────────┐
// │ ID │ Name             │ health   │ connectivity │lifecycle │ Reason                             │
// ├────┼──────────────────┼──────────┼─────────────┼──────────┼────────────────────────────────────┤
// │101 │ North Field      │ HEALTHY  │ ONLINE       │ ACTIVE   │ No active alerts, all sensors OK   │
// │102 │ South Field      │ WARNING  │ ONLINE       │ ACTIVE   │ Low moisture + weak sensor alerts  │
// │103 │ East Orchard     │ CRITICAL │ OFFLINE      │ ACTIVE   │ Sensor broken + critical temp      │
// │104 │ West Greenhouse  │ WARNING  │ ONLINE       │ ACTIVE   │ High humidity alert ongoing        │
// │105 │ Archived Field   │ HEALTHY  │ OFFLINE      │ ARCHIVED │ No sensors, field retired          │
// └────┴──────────────────┴──────────┴─────────────┴──────────┴────────────────────────────────────┘

const SNAPSHOTS_APR14 = {
  101: {
    soilMoisture: 38, soilTemperature: 17, airTemperature: 22,
    airHumidity: 54, lux: 31000,
    health: 0, connectivity: 0, lifecycle: 0,
    activeSensors: 3, totalSensors: 3,
    history: halfHourlyHistory(101, { soilMoisture:38, soilTemperature:17, airTemperature:22, airHumidity:54, lux:31000 }),
  },
  102: {
    soilMoisture: 21, soilTemperature: 19, airTemperature: 27,
    airHumidity: 49, lux: 38000,
    health: 1, connectivity: 0, lifecycle: 0,
    activeSensors: 2, totalSensors: 3,
    history: halfHourlyHistory(102, { soilMoisture:21, soilTemperature:19, airTemperature:27, airHumidity:49, lux:38000 }),
  },
  103: {
    soilMoisture: 18, soilTemperature: 22, airTemperature: 38,
    airHumidity: 35, lux: 52000,
    health: 2, connectivity: 1, lifecycle: 0,
    activeSensors: 1, totalSensors: 3,
    history: halfHourlyHistory(103, { soilMoisture:18, soilTemperature:22, airTemperature:38, airHumidity:35, lux:52000 }),
  },
  104: {
    soilMoisture: 55, soilTemperature: 23, airTemperature: 26,
    airHumidity: 82, lux: 14000,
    health: 1, connectivity: 0, lifecycle: 0,
    activeSensors: 3, totalSensors: 3,
    history: halfHourlyHistory(104, { soilMoisture:55, soilTemperature:23, airTemperature:26, airHumidity:82, lux:14000 }),
  },
  105: {
    soilMoisture: null, soilTemperature: null, airTemperature: null,
    airHumidity: null, lux: null,
    health: 0, connectivity: 1, lifecycle: 3,
    activeSensors: 0, totalSensors: 0,
    history: [],
  },
};

// ─── APR 15 delta ─────────────────────────────────────────────────────────────
// Changes vs APR 14:
//   102: irrigation ran overnight → soilMoisture 21→29; broken sensor replaced → activeSensors 2→3
//   103: maintenance replaced broken gateway sensor → connectivity OFFLINE→ONLINE;
//        temp slightly down to 36 but still critical; activeSensors 1→2
//   104: dehumidifiers engaged → airHumidity 82→71; still WARNING (threshold 80, now below)

const SNAPSHOTS_APR15 = {
  // unchanged fields — same snapshot as APR14, only history added
  101: {
    soilMoisture: 38, soilTemperature: 17, airTemperature: 22,
    airHumidity: 54, lux: 31000,
    health: 0, connectivity: 0, lifecycle: 0,
    activeSensors: 3, totalSensors: 3,
    history: singleDayHistory(101, APR_15, { soilMoisture:38, soilTemperature:17, airTemperature:22, airHumidity:54, lux:31000 }),
  },
  102: {
    soilMoisture: 29, soilTemperature: 19, airTemperature: 27,
    airHumidity: 49, lux: 39000,
    health: 1, connectivity: 0, lifecycle: 0,
    activeSensors: 3, totalSensors: 3,
    history: singleDayHistory(102, APR_15, { soilMoisture:29, soilTemperature:19, airTemperature:27, airHumidity:49, lux:39000 }),
  },
  103: {
    soilMoisture: 20, soilTemperature: 21, airTemperature: 36,
    airHumidity: 37, lux: 50000,
    health: 2, connectivity: 0, lifecycle: 0,
    activeSensors: 2, totalSensors: 3,
    history: singleDayHistory(103, APR_15, { soilMoisture:20, soilTemperature:21, airTemperature:36, airHumidity:37, lux:50000 }),
  },
  104: {
    soilMoisture: 55, soilTemperature: 23, airTemperature: 26,
    airHumidity: 71, lux: 14500,
    health: 1, connectivity: 0, lifecycle: 0,
    activeSensors: 3, totalSensors: 3,
    history: singleDayHistory(104, APR_15, { soilMoisture:55, soilTemperature:23, airTemperature:26, airHumidity:71, lux:14500 }),
  },
  105: {
    soilMoisture: null, soilTemperature: null, airTemperature: null,
    airHumidity: null, lux: null,
    health: 0, connectivity: 1, lifecycle: 3,
    activeSensors: 0, totalSensors: 0,
    history: [],
  },
};

// ─── APR 16 delta ─────────────────────────────────────────────────────────────
// Changes vs APR 15:
//   101: slight moisture dip → WARNING; soilMoisture 38→29
//   103: shading + cooling worked → airTemperature 36→28, health CRITICAL→HEALTHY;
//        last sensor restored → activeSensors 2→3
//   104: humidity alert resolved manually → health WARNING→HEALTHY; airHumidity 71→66

const SNAPSHOTS_APR16 = {
  101: {
    soilMoisture: 29, soilTemperature: 17, airTemperature: 23,
    airHumidity: 52, lux: 30500,
    health: 1, connectivity: 0, lifecycle: 0,
    activeSensors: 3, totalSensors: 3,
    history: singleDayHistory(101, APR_16, { soilMoisture:29, soilTemperature:17, airTemperature:23, airHumidity:52, lux:30500 }),
  },
  102: {
    soilMoisture: 29, soilTemperature: 19, airTemperature: 27,
    airHumidity: 49, lux: 39000,
    health: 1, connectivity: 0, lifecycle: 0,
    activeSensors: 3, totalSensors: 3,
    history: singleDayHistory(102, APR_16, { soilMoisture:29, soilTemperature:19, airTemperature:27, airHumidity:49, lux:39000 }),
  },
  103: {
    soilMoisture: 27, soilTemperature: 19, airTemperature: 28,
    airHumidity: 45, lux: 47000,
    health: 0, connectivity: 0, lifecycle: 0,
    activeSensors: 3, totalSensors: 3,
    history: singleDayHistory(103, APR_16, { soilMoisture:27, soilTemperature:19, airTemperature:28, airHumidity:45, lux:47000 }),
  },
  104: {
    soilMoisture: 54, soilTemperature: 22, airTemperature: 25,
    airHumidity: 66, lux: 15000,
    health: 0, connectivity: 0, lifecycle: 0,
    activeSensors: 3, totalSensors: 3,
    history: singleDayHistory(104, APR_16, { soilMoisture:54, soilTemperature:22, airTemperature:25, airHumidity:66, lux:15000 }),
  },
  105: {
    soilMoisture: null, soilTemperature: null, airTemperature: null,
    airHumidity: null, lux: null,
    health: 0, connectivity: 1, lifecycle: 3,
    activeSensors: 0, totalSensors: 0,
    history: [],
  },
};

// ─── Response builders ────────────────────────────────────────────────────────

function buildFullResponse(snapshots, serverTime, nextCursor) {
  return {
    serverTime,
    nextCursor,
    fields: FIELDS_BASE.map((f) => {
      const { history, ...snapshotFields } = snapshots[f.id] ?? {};
      return {
        ...f,
        snapshot: snapshotFields,
        history: history ?? [],
      };
    }),
    fieldPoints: FIELD_POINTS,
  };
}

function buildDeltaResponse(snapshots, serverTime, nextCursor) {
  return {
    serverTime,
    nextCursor,
    fields: Object.entries(snapshots).map(([id, snap]) => {
      const { history, ...snapshotFields } = snap;
      return {
        ...FIELDS_BASE.find((f) => f.id === Number(id)),
        snapshot: snapshotFields,
        history: history ?? [],
      };
    }),
    fieldPoints: [],
  };
}

function getFieldsResponse(since) {
  if (!since || since < APR_14) return buildFullResponse(SNAPSHOTS_APR14, APR_14, APR_15);
  if (since < APR_15)           return buildDeltaResponse(SNAPSHOTS_APR15, APR_15, APR_16);
  if (since < APR_16)           return buildDeltaResponse(SNAPSHOTS_APR16, APR_16, APR_16 + 86400000);
  return null;
}

// ═══════════════════════════════════════════════════════════════════════════════
// ALERTS  (initial seed — written to alerts.json on first boot)
// ═══════════════════════════════════════════════════════════════════════════════
//
// lifecycle: 0=ACTIVE  1=RESOLVED  2=DISMISSED
// severity:  1=LOW     2=MEDIUM    3=CRITICAL
// type:      0=SOIL_MOISTURE  1=SOIL_TEMP  2=AIR_TEMP  3=AIR_HUMIDITY
//            4=LUX            5=SENSOR_ISSUE
// verification: 0=AUTO  1=MANUAL
//
// ┌──────┬───────┬──────────┬────────────┬───────────┬──────────────────────────────────┐
// │  ID  │ Field │ severity │  lifecycle │   type    │ Summary                          │
// ├──────┼───────┼──────────┼────────────┼───────────┼──────────────────────────────────┤
// │ 3001 │  102  │  LOW     │  ACTIVE    │ MOISTURE  │ Soil moisture below threshold     │
// │ 3002 │  102  │  MEDIUM  │  ACTIVE    │ SENSOR    │ Weak signal on SF-AT-01          │
// │ 3003 │  103  │  CRITICAL│  ACTIVE    │ SENSOR    │ Gateway sensor dead → offline    │
// │ 3004 │  103  │  CRITICAL│  ACTIVE    │ AIR_TEMP  │ Air temp 38°C, crop damage risk  │
// │ 3005 │  104  │  MEDIUM  │  RESOLVED  │ HUMIDITY  │ High humidity — now fixed        │
// │ 3006 │  101  │  LOW     │  DISMISSED │ LUX       │ Lux spike — sensor artefact      │
// │ 3007 │  104  │  MEDIUM  │  ACTIVE    │ HUMIDITY  │ Humidity still elevated          │
// └──────┴───────┴──────────┴────────────┴───────────┴──────────────────────────────────┘

const INITIAL_ALERTS = [
  {
    id: 3001, fieldId: 102,
    title: "Low soil moisture",
    description: "Soil moisture in South Field has dropped to 21%, below the minimum threshold of 25%. Crop stress may develop if not addressed within 24 hours.",
    recommendation: "Schedule irrigation for the next available window. Target moisture level: 30–40%.",
    currentValue: "21", unit: "%", threshold: "25",
    expectedRange: "25-45", deviation: "-4",
    sensorId: "SF-SM-01",
    detectedAt: APR_14 - 3 * 3600000,
    lifecycle: 0, severity: 1, verification: 0, type: 0,
    updatedAt: 0,
  },
  {
    id: 3002, fieldId: 102,
    title: "Weak sensor signal",
    description: "Sensor SF-AT-01 is transmitting with degraded signal strength. Readings may be inaccurate or delayed, reducing data reliability for the field.",
    recommendation: "Inspect sensor mounting and antenna orientation. Consider relocating 3 m closer to the nearest gateway repeater.",
    currentValue: null, unit: null, threshold: null,
    expectedRange: null, deviation: null,
    sensorId: "SF-AT-01",
    detectedAt: APR_14 - 8 * 3600000,
    lifecycle: 0, severity: 2, verification: 0, type: 5,
    updatedAt: 0,
  },
  {
    id: 3003, fieldId: 103,
    title: "Sensor offline — no data",
    description: "Gateway sensor EO-GW-01 in East Orchard has stopped transmitting entirely. The field has lost primary connectivity and sensor readings are unavailable.",
    recommendation: "Dispatch maintenance immediately to inspect power supply, replace fuse or sensor unit. Restore connectivity before end of business day.",
    currentValue: null, unit: null, threshold: null,
    expectedRange: null, deviation: null,
    sensorId: "EO-GW-01",
    detectedAt: APR_14 - 18 * 3600000,
    lifecycle: 0, severity: 3, verification: 0, type: 5,
    updatedAt: 0,
  },
  {
    id: 3004, fieldId: 103,
    title: "Critical air temperature",
    description: "Air temperature in East Orchard has reached 38 °C, well above the safe maximum of 32 °C. Risk of severe heat stress and irreversible crop damage within hours.",
    recommendation: "Activate emergency shading on rows 4–9 and run overhead cooling irrigation immediately. Monitor hourly until temperature drops below 30 °C.",
    currentValue: "38", unit: "°C", threshold: "32",
    expectedRange: "15-30", deviation: "+6",
    sensorId: "EO-AT-01",
    detectedAt: APR_14 - 6 * 3600000,
    lifecycle: 0, severity: 3, verification: 1, type: 2,
    updatedAt: 0,
  },
  {
    id: 3005, fieldId: 104,
    title: "High air humidity — resolved",
    description: "Air humidity in West Greenhouse peaked at 89%, triggering fungal disease risk (botrytis, powdery mildew). Dehumidifiers were activated and humidity has been brought back to a safe range.",
    recommendation: "Continue monitoring humidity daily. Check dehumidifier filters weekly. Keep set-point at 65% RH.",
    currentValue: "89", unit: "%", threshold: "80",
    expectedRange: "50-75", deviation: "+9",
    sensorId: "WG-AH-01",
    detectedAt: APR_14 - 5 * 86400000,
    lifecycle: 1, severity: 2, verification: 1, type: 3,
    updatedAt: APR_14 - 2 * 86400000,
  },
  {
    id: 3006, fieldId: 101,
    title: "Lux spike — dismissed",
    description: "Sensor NF-LX-01 reported an abnormally high light intensity of 98,000 lux. Manual inspection confirmed the reading was caused by temporary lens condensation, not actual sunlight intensity.",
    recommendation: "No action required. Clean sensor lens monthly and after heavy rain.",
    currentValue: "98000", unit: "lux", threshold: "80000",
    expectedRange: "5000-75000", deviation: "+18000",
    sensorId: "NF-LX-01",
    detectedAt: APR_14 - 10 * 86400000,
    lifecycle: 2, severity: 1, verification: 1, type: 4,
    updatedAt: APR_14 - 9 * 86400000,
  },
  {
    id: 3007, fieldId: 104,
    title: "Elevated air humidity",
    description: "Air humidity in West Greenhouse has been above 80% for the past 6 hours. Sustained high humidity significantly increases risk of botrytis and powdery mildew on crops.",
    recommendation: "Increase ventilation fan speed to maximum. Inspect drip lines for over-watering. Target humidity: 55–75%.",
    currentValue: "82", unit: "%", threshold: "80",
    expectedRange: "50-75", deviation: "+2",
    sensorId: "WG-AH-02",
    detectedAt: APR_14 - 6 * 3600000,
    lifecycle: 0, severity: 2, verification: 0, type: 3,
    updatedAt: 0,
  },
];

// ═══════════════════════════════════════════════════════════════════════════════
// TASKS  (initial seed — written to tasks.json on first boot)
// ═══════════════════════════════════════════════════════════════════════════════
//
// status:   0=TODO  1=IN_PROGRESS  2=DONE  3=CANCELLED
// priority: 0=LOW   1=MEDIUM       2=HIGH
//
// ┌──────┬───────┬──────────┬─────────────┬──────────────────────────────────────────┐
// │  ID  │ Field │ priority │   status    │ Summary                                  │
// ├──────┼───────┼──────────┼─────────────┼──────────────────────────────────────────┤
// │ 4001 │  103  │  HIGH    │ IN_PROGRESS │ Replace broken gateway sensor            │
// │ 4002 │  103  │  HIGH    │    TODO     │ Emergency shading + cooling irrigation   │
// │ 4003 │  102  │  MEDIUM  │ IN_PROGRESS │ Run irrigation cycle                     │
// │ 4004 │  102  │  LOW     │    TODO     │ Relocate weak-signal sensor              │
// │ 4005 │  104  │  MEDIUM  │    DONE     │ Dehumidifier service (linked to 3005)    │
// │ 4006 │  101  │  LOW     │  CANCELLED  │ Investigate lux spike (linked to 3006)   │
// │ 4007 │  104  │  HIGH    │    TODO     │ Install second ventilation fan           │
// └──────┴───────┴──────────┴─────────────┴──────────────────────────────────────────┘

const INITIAL_TASKS = [
  {
    id: 4001,
    title: "Replace broken gateway sensor — East Orchard",
    description: "Sensor EO-GW-01 has failed completely and East Orchard has lost all connectivity. Replace the unit and restore field connectivity before end of business day.",
    notes: "Spare sensor units are in storage room B, shelf 3. Bring a gateway config cable and the signal tester.",
    fieldId: 103, alertId: 3003,
    timeDue: APR_14 + 12 * 3600000,
    timeCreated: APR_14 - 17 * 3600000,
    status: 1, priority: 2, type: 5,
    alertDeleted: false, updatedAt: 0,
  },
  {
    id: 4002,
    title: "Deploy emergency shading — East Orchard",
    description: "Air temperature at 38 °C, exceeding the 32 °C threshold. Unfurl shade nets over rows 4–9 and activate overhead misting system immediately to prevent crop loss.",
    notes: "",
    fieldId: 103, alertId: 3004,
    timeDue: APR_14 + 4 * 3600000,
    timeCreated: APR_14 - 5 * 3600000,
    status: 0, priority: 2, type: 2,
    alertDeleted: false, updatedAt: 0,
  },
  {
    id: 4003,
    title: "Run irrigation cycle — South Field",
    description: "Soil moisture at 21%, below the 25% threshold. Open valve V-102 and run a 3-hour drip cycle to bring moisture back above 30%.",
    notes: "Check filter on valve V-102 first — it was partially blocked last week.",
    fieldId: 102, alertId: 3001,
    timeDue: APR_14 + 8 * 3600000,
    timeCreated: APR_14 - 2 * 3600000,
    status: 1, priority: 1, type: 0,
    alertDeleted: false, updatedAt: 0,
  },
  {
    id: 4004,
    title: "Relocate sensor SF-AT-01 — South Field",
    description: "Sensor SF-AT-01 has weak signal strength. Move it 3 metres closer to the nearest gateway repeater and re-test signal quality after relocation.",
    notes: "",
    fieldId: 102, alertId: 3002,
    timeDue: APR_14 + 2 * 86400000,
    timeCreated: APR_14 - 7 * 3600000,
    status: 0, priority: 0, type: 5,
    alertDeleted: false, updatedAt: 0,
  },
  {
    id: 4005,
    title: "Service dehumidifier units — West Greenhouse",
    description: "Clean filters on both dehumidifier units and verify set-point is 65% RH. Log service in the maintenance register.",
    notes: "Completed Apr 12. Both units running normally. Humidity dropped from 89% to 71% over two days.",
    fieldId: 104, alertId: 3005,
    timeDue: APR_14 - 2 * 86400000,
    timeCreated: APR_14 - 6 * 86400000,
    status: 2, priority: 1, type: 3,
    alertDeleted: false, updatedAt: APR_14 - 2 * 86400000,
  },
  {
    id: 4006,
    title: "Investigate lux spike on NF-LX-01 — North Field",
    description: "Alert 3006 reported 98,000 lux on NF-LX-01. Inspect sensor lens for contamination or physical damage.",
    notes: "Alert dismissed — confirmed lens condensation after heavy rain. No hardware fault found. Task cancelled.",
    fieldId: 101, alertId: 3006,
    timeDue: APR_14 - 8 * 86400000,
    timeCreated: APR_14 - 10 * 86400000,
    status: 3, priority: 0, type: 4,
    alertDeleted: false, updatedAt: APR_14 - 9 * 86400000,
  },
  {
    id: 4007,
    title: "Install additional ventilation fan — West Greenhouse",
    description: "The current single exhaust fan is insufficient to keep humidity below 80% during warm periods. Install a second 1200 CFM fan on the north wall. Coordinate with the electrician for 3-phase wiring.",
    notes: "",
    fieldId: 104, alertId: 3007,
    timeDue: APR_14 + 3 * 86400000,
    timeCreated: APR_14 - 5 * 3600000,
    status: 0, priority: 2, type: 3,
    alertDeleted: false, updatedAt: 0,
  },
];

// ═══════════════════════════════════════════════════════════════════════════════
// SENSORS
// ═══════════════════════════════════════════════════════════════════════════════
//
// 3 sensors per field = 15 total. Coordinates are inside each polygon.
// state:   0=WORKING  1=WEAK_SIGNAL  2=BROKEN  3=UNKNOWN
// battery: 0=LOW      1=MEDIUM       2=HIGH
//
// 101 North Field     — all WORKING, battery healthy (no alerts)
// 102 South Field     — SF-SM-01 working, SF-AT-01 WEAK_SIGNAL (alert 3002), SF-AH-01 working
// 103 East Orchard    — EO-GW-01 BROKEN (alert 3003, causes offline), EO-AT-01 working (reports 38°C),
//                       EO-SM-01 UNKNOWN (can't report via broken gateway)
// 104 West Greenhouse — all WORKING (humidity is env issue, not sensor fault)
// 105 Archived Field  — all UNKNOWN + LOW battery (powered down)

const INITIAL_SENSORS = [
  // ── 101 North Field (inside polygon ~50.286±0.0003, 71.6237±0.0005) ──
  { id: 2001, fieldId: 101, name: "NF-SM-01", latitude: 50.286100, longitude: 71.623650, battery: 2, state: 0 },
  { id: 2002, fieldId: 101, name: "NF-AT-01", latitude: 50.286200, longitude: 71.623850, battery: 1, state: 0 },
  { id: 2003, fieldId: 101, name: "NF-LX-01", latitude: 50.285950, longitude: 71.623750, battery: 2, state: 0 },

  // ── 102 South Field (inside polygon ~50.284±0.0003, 71.6236±0.0005) ──
  { id: 2004, fieldId: 102, name: "SF-SM-01", latitude: 50.284200, longitude: 71.623550, battery: 1, state: 0 },
  { id: 2005, fieldId: 102, name: "SF-AT-01", latitude: 50.284100, longitude: 71.623750, battery: 0, state: 1 },
  { id: 2006, fieldId: 102, name: "SF-AH-01", latitude: 50.284050, longitude: 71.623600, battery: 2, state: 0 },

  // ── 103 East Orchard (inside polygon ~50.285±0.0003, 71.6246±0.0005) ──
  { id: 2007, fieldId: 103, name: "EO-GW-01", latitude: 50.285050, longitude: 71.624600, battery: 0, state: 2 },
  { id: 2008, fieldId: 103, name: "EO-AT-01", latitude: 50.284950, longitude: 71.624750, battery: 1, state: 0 },
  { id: 2009, fieldId: 103, name: "EO-SM-01", latitude: 50.284850, longitude: 71.624600, battery: 1, state: 3 },

  // ── 104 West Greenhouse (inside polygon ~50.285±0.0002, 71.6226±0.0003) ──
  { id: 2010, fieldId: 104, name: "WG-AH-01", latitude: 50.285100, longitude: 71.622600, battery: 2, state: 0 },
  { id: 2011, fieldId: 104, name: "WG-AH-02", latitude: 50.285000, longitude: 71.622750, battery: 2, state: 0 },
  { id: 2012, fieldId: 104, name: "WG-ST-01", latitude: 50.284900, longitude: 71.622650, battery: 1, state: 0 },

  // ── 105 Archived Field (inside polygon ~50.287±0.0002, 71.6250±0.0004) ──
  { id: 2013, fieldId: 105, name: "AF-SM-01", latitude: 50.286950, longitude: 71.624800, battery: 0, state: 3 },
  { id: 2014, fieldId: 105, name: "AF-AT-01", latitude: 50.287050, longitude: 71.624950, battery: 0, state: 3 },
  { id: 2015, fieldId: 105, name: "AF-LX-01", latitude: 50.286900, longitude: 71.625100, battery: 0, state: 3 },
];

// ═══════════════════════════════════════════════════════════════════════════════
// USER
// ═══════════════════════════════════════════════════════════════════════════════

const FARM_DETAILS = {
  id: 1,
  farmName: "Green Valley Farm",
  // working: 2001,2002,2003,2004,2006,2008,2010,2011,2012 = 9 working
  // weak signal counts as active: 2005 = +1  → 10 active
  // broken: 2007, unknown: 2009,2013,2014,2015 → not counted
  activeSensors: 10,
  totalSensors: 15,
};

const USER_DETAILS = {
  id: 1,
  farmId: 1,
  role: "ADMIN",
  name: "John",
  surname: "Smith",
  email: "john.smith@greenvalleyfarm.com",
  company: "Green Valley Farm",
};

// ═══════════════════════════════════════════════════════════════════════════════
// GENERIC DB HELPERS
// ═══════════════════════════════════════════════════════════════════════════════

function loadDb(dbPath, initial) {
  if (!fs.existsSync(dbPath)) {
    fs.writeFileSync(dbPath, JSON.stringify(initial, null, 2), "utf-8");
    return JSON.parse(JSON.stringify(initial));
  }
  return JSON.parse(fs.readFileSync(dbPath, "utf-8"));
}

function saveDb(dbPath, data) {
  fs.writeFileSync(dbPath, JSON.stringify(data, null, 2), "utf-8");
}

// ═══════════════════════════════════════════════════════════════════════════════
// ROUTES — ALERTS
// ═══════════════════════════════════════════════════════════════════════════════

app.get("/alerts", (req, res) => {
  const alerts = loadDb(ALERTS_DB, INITIAL_ALERTS);
  const since = req.query.since ? Number(req.query.since) : null;
  const result = since ? alerts.filter((a) => a.updatedAt > since) : alerts;
  res.json({ alerts: result });
});

app.patch("/alerts/:id/lifecycle", (req, res) => {
  const alerts = loadDb(ALERTS_DB, INITIAL_ALERTS);
  const id = Number(req.params.id);
  const { lifecycle } = req.body;
  if (lifecycle === undefined)
    return res.status(400).json({ success: false, message: "lifecycle is required" });
  const alert = alerts.find((a) => a.id === id);
  if (!alert)
    return res.status(404).json({ success: false, message: "Alert not found" });
  alert.lifecycle = lifecycle;
  alert.updatedAt = Date.now();
  saveDb(ALERTS_DB, alerts);
  res.json({ success: true, message: "Alert updated successfully." });
});

app.delete("/alerts/:id", (req, res) => {
  const alerts = loadDb(ALERTS_DB, INITIAL_ALERTS);
  const id = Number(req.params.id);
  const index = alerts.findIndex((a) => a.id === id);
  if (index === -1)
    return res.status(404).json({ success: false, message: "Alert not found" });
  alerts.splice(index, 1);
  saveDb(ALERTS_DB, alerts);
  res.json({ success: true, message: "Alert deleted successfully." });
});

// ═══════════════════════════════════════════════════════════════════════════════
// ROUTES — TASKS
// ═══════════════════════════════════════════════════════════════════════════════

app.get("/tasks", (req, res) => {
  const tasks = loadDb(TASKS_DB, INITIAL_TASKS);
  const since = req.query.since ? Number(req.query.since) : null;
  const result = since ? tasks.filter((t) => t.updatedAt > since) : tasks;
  res.json({ tasks: result });
});

app.patch("/tasks/:id/status", (req, res) => {
  const tasks = loadDb(TASKS_DB, INITIAL_TASKS);
  const id = Number(req.params.id);
  const { status } = req.body;
  if (status === undefined)
    return res.status(400).json({ success: false, message: "status is required" });
  const task = tasks.find((t) => t.id === id);
  if (!task)
    return res.status(404).json({ success: false, message: "Task not found" });
  task.status = status;
  task.updatedAt = Date.now();
  saveDb(TASKS_DB, tasks);
  res.json({ success: true, message: "Task status updated successfully." });
});

app.patch("/tasks/:id/note", (req, res) => {
  const tasks = loadDb(TASKS_DB, INITIAL_TASKS);
  const id = Number(req.params.id);
  const { note } = req.body;
  if (note === undefined)
    return res.status(400).json({ success: false, message: "note is required" });
  const task = tasks.find((t) => t.id === id);
  if (!task)
    return res.status(404).json({ success: false, message: "Task not found" });
  task.notes = note;
  task.updatedAt = Date.now();
  saveDb(TASKS_DB, tasks);
  res.json({ success: true, message: "Task note updated successfully." });
});

app.put("/tasks/:id", (req, res) => {
  const tasks = loadDb(TASKS_DB, INITIAL_TASKS);
  const id = Number(req.params.id);
  const index = tasks.findIndex((t) => t.id === id);
  if (index === -1)
    return res.status(404).json({ success: false, message: "Task not found" });
  tasks[index] = { ...req.body, id, updatedAt: Date.now() };
  saveDb(TASKS_DB, tasks);
  res.json({ success: true, message: "Task updated successfully." });
});

app.delete("/tasks/:id", (req, res) => {
  const tasks = loadDb(TASKS_DB, INITIAL_TASKS);
  const id = Number(req.params.id);
  const index = tasks.findIndex((t) => t.id === id);
  if (index === -1)
    return res.status(404).json({ success: false, message: "Task not found" });
  tasks.splice(index, 1);
  saveDb(TASKS_DB, tasks);
  res.json({ success: true, message: "Task deleted successfully." });
});

app.post("/tasks", (req, res) => {
  const tasks = loadDb(TASKS_DB, INITIAL_TASKS);
  const maxId = tasks.reduce((max, t) => Math.max(max, t.id), 0);
  const newTask = { ...req.body, id: maxId + 1, updatedAt: Date.now() };
  tasks.push(newTask);
  saveDb(TASKS_DB, tasks);
  res.status(201).json({ success: true, message: "Task created successfully." });
});

// ═══════════════════════════════════════════════════════════════════════════════
// ROUTES — SENSORS
// ═══════════════════════════════════════════════════════════════════════════════

app.get("/sensors", (req, res) => {
  res.json({ sensors: INITIAL_SENSORS });
});

// ═══════════════════════════════════════════════════════════════════════════════
// ROUTES — USER
// ═══════════════════════════════════════════════════════════════════════════════

app.get("/user/farm", (req, res) => res.json(FARM_DETAILS));
app.get("/user/details", (req, res) => res.json(USER_DETAILS));

// ═══════════════════════════════════════════════════════════════════════════════
// ROUTES — FIELDS
// ═══════════════════════════════════════════════════════════════════════════════

app.get("/fields/sync", (req, res) => {
  const since = req.query.since ? Number(req.query.since) : null;
  const response = getFieldsResponse(since);
  if (!response) {
    return res.json({ serverTime: APR_16, nextCursor: APR_16 + 86400000, fields: [], fieldPoints: [] });
  }
  res.json(response);
});

// ─── Start ────────────────────────────────────────────────────────────────────
const PORT = 3001;
app.listen(PORT, () => {
  console.log(`\nMock server running at http://localhost:${PORT}\n`);
  console.log("ALERTS");
  console.log(`  GET    /alerts`);
  console.log(`  PATCH  /alerts/:id/lifecycle`);
  console.log(`  DELETE /alerts/:id`);
  console.log("\nTASKS");
  console.log(`  GET    /tasks`);
  console.log(`  PATCH  /tasks/:id/status`);
  console.log(`  PATCH  /tasks/:id/note`);
  console.log(`  PUT    /tasks/:id`);
  console.log(`  DELETE /tasks/:id`);
  console.log(`  POST   /tasks`);
  console.log("\nSENSORS");
  console.log(`  GET    /sensors`);
  console.log("\nUSER");
  console.log(`  GET    /user/farm`);
  console.log(`  GET    /user/details`);
  console.log("\nFIELDS  (since-aware sync)");
  console.log(`  GET    /fields/sync`);
  console.log(`         since=null / <Apr 14  →  full APR_14 dataset + history`);
  console.log(`         since in [Apr14,Apr15) →  delta: fields 102,103,104 changed`);
  console.log(`         since in [Apr15,Apr16) →  delta: fields 101,103,104 changed`);
  console.log(`         since >= Apr16          →  empty (client up to date)`);
});
