package com.example.agristation1.fakedata

import com.example.agristation1.data.sensorDetails.SensorBattery
import com.example.agristation1.data.sensorDetails.SensorDetails
import com.example.agristation1.data.sensorDetails.SensorState

object FakeSensorData {
    val sensorDetailsList = listOf(
        SensorDetails(
            id = 2001,
            fieldId = 101,
            name = "NF-SM-01",
            latitude = 50.286120,
            longitude = 71.623620,
            battery = SensorBattery.HIGH,
            state = SensorState.WORKING
        ),
        SensorDetails(
            id = 2002,
            fieldId = 101,
            name = "NF-AT-01",
            latitude = 50.286000,
            longitude = 71.623780,
            battery = SensorBattery.MEDIUM,
            state = SensorState.WORKING
        ),
        SensorDetails(
            id = 2003,
            fieldId = 101,
            name = "NF-LX-01",
            latitude = 50.285950,
            longitude = 71.623560,
            battery = SensorBattery.HIGH,
            state = SensorState.WORKING
        ),
        SensorDetails(
            id = 2004,
            fieldId = 102,
            name = "SF-SM-01",
            latitude = 50.284180,
            longitude = 71.623560,
            battery = SensorBattery.MEDIUM,
            state = SensorState.WEAK_SIGNAL
        ),
        SensorDetails(
            id = 2005,
            fieldId = 102,
            name = "SF-AT-01",
            latitude = 50.284060,
            longitude = 71.623760,
            battery = SensorBattery.LOW,
            state = SensorState.BROKEN
        ),
        SensorDetails(
            id = 2006,
            fieldId = 103,
            name = "EO-SM-01",
            latitude = 50.285050,
            longitude = 71.624560,
            battery = SensorBattery.HIGH,
            state = SensorState.WORKING
        ),
        SensorDetails(
            id = 2007,
            fieldId = 103,
            name = "EO-AH-01",
            latitude = 50.284980,
            longitude = 71.624720,
            battery = SensorBattery.MEDIUM,
            state = SensorState.WORKING
        ),
        SensorDetails(
            id = 2008,
            fieldId = 103,
            name = "EO-LX-01",
            latitude = 50.284900,
            longitude = 71.624520,
            battery = SensorBattery.LOW,
            state = SensorState.BROKEN
        ),
        SensorDetails(
            id = 2009,
            fieldId = 104,
            name = "WG-ST-01",
            latitude = 50.285040,
            longitude = 71.622620,
            battery = SensorBattery.HIGH,
            state = SensorState.WORKING
        ),
        SensorDetails(
            id = 2010,
            fieldId = 104,
            name = "WG-AH-01",
            latitude = 50.284980,
            longitude = 71.622780,
            battery = SensorBattery.MEDIUM,
            state = SensorState.WORKING
        )
    )
}