package com.example.agristation1.fakedata

import com.example.agristation1.data.fieldDetails.FieldConnectivity
import com.example.agristation1.data.fieldDetails.FieldDetails
import com.example.agristation1.data.fieldDetails.FieldHealth
import com.example.agristation1.data.fieldDetails.FieldLifecycle
import com.example.agristation1.data.fieldDetails.FieldPoints
import com.example.agristation1.data.fieldDetails.FieldWithPoints

object FakeCoordinates {
    val fieldWithPointsList = listOf(
        FieldWithPoints(
            field = FieldDetails(
                id = 101,
                farmId = 1,
                title = "North Field",
                area = 12.5,
                type = "WHEAT",
                soilMoisture = 41.0,
                lastValidSoilMoisture = 41.0,
                soilTemperature = 18.0,
                lastValidSoilTemperature = 18.0,
                airTemperature = 24.0,
                lastValidAirTemperature = 24.0,
                airHumidity = 58.0,
                lastValidAirHumidity = 58.0,
                lux = 32000.0,
                lastValidLux = 32000.0,
                totalSensors = 3,
                activeSensors = 3,
                health = FieldHealth.HEALTHY,
                connectivity = FieldConnectivity.ONLINE,
                lifecycle = FieldLifecycle.ACTIVE
            ),
            points = listOf(
                FieldPoints(fieldId = 101, pointOrder = 0, latitude = 50.286100, longitude = 71.623300),
                FieldPoints(fieldId = 101, pointOrder = 1, latitude = 50.286450, longitude = 71.623650),
                FieldPoints(fieldId = 101, pointOrder = 2, latitude = 50.286250, longitude = 71.624100),
                FieldPoints(fieldId = 101, pointOrder = 3, latitude = 50.285850, longitude = 71.624000),
                FieldPoints(fieldId = 101, pointOrder = 4, latitude = 50.285750, longitude = 71.623500)
            )
        ),
        FieldWithPoints(
            field = FieldDetails(
                id = 102,
                farmId = 1,
                title = "South Field",
                area = 9.8,
                type = "CORN",
                soilMoisture = 27.0,
                lastValidSoilMoisture = 29.0,
                soilTemperature = 20.0,
                lastValidSoilTemperature = 20.0,
                airTemperature = 29.0,
                lastValidAirTemperature = 29.0,
                airHumidity = 46.0,
                lastValidAirHumidity = 46.0,
                lux = 41000.0,
                lastValidLux = 41000.0,
                totalSensors = 2,
                activeSensors = 1,
                health = FieldHealth.WARNING,
                connectivity = FieldConnectivity.PARTIAL,
                lifecycle = FieldLifecycle.ACTIVE
            ),
            points = listOf(
                FieldPoints(fieldId = 102, pointOrder = 0, latitude = 50.284200, longitude = 71.623250),
                FieldPoints(fieldId = 102, pointOrder = 1, latitude = 50.284550, longitude = 71.623550),
                FieldPoints(fieldId = 102, pointOrder = 2, latitude = 50.284350, longitude = 71.624000),
                FieldPoints(fieldId = 102, pointOrder = 3, latitude = 50.283950, longitude = 71.623900),
                FieldPoints(fieldId = 102, pointOrder = 4, latitude = 50.283850, longitude = 71.623450)
            )
        ),
        FieldWithPoints(
            field = FieldDetails(
                id = 103,
                farmId = 1,
                title = "East Orchard",
                area = 6.2,
                type = "APPLE",
                soilMoisture = 35.0,
                lastValidSoilMoisture = 35.0,
                soilTemperature = 16.0,
                lastValidSoilTemperature = 16.0,
                airTemperature = 22.0,
                lastValidAirTemperature = 22.0,
                airHumidity = 63.0,
                lastValidAirHumidity = 63.0,
                lux = 28000.0,
                lastValidLux = 28000.0,
                totalSensors = 3,
                activeSensors = 2,
                health = FieldHealth.WARNING,
                connectivity = FieldConnectivity.PARTIAL,
                lifecycle = FieldLifecycle.ACTIVE
            ),
            points = listOf(
                FieldPoints(fieldId = 103, pointOrder = 0, latitude = 50.285000, longitude = 71.624250),
                FieldPoints(fieldId = 103, pointOrder = 1, latitude = 50.285350, longitude = 71.624550),
                FieldPoints(fieldId = 103, pointOrder = 2, latitude = 50.285150, longitude = 71.624950),
                FieldPoints(fieldId = 103, pointOrder = 3, latitude = 50.284800, longitude = 71.624900),
                FieldPoints(fieldId = 103, pointOrder = 4, latitude = 50.284700, longitude = 71.624450)
            )
        ),
        FieldWithPoints(
            field = FieldDetails(
                id = 104,
                farmId = 1,
                title = "West Greenhouse",
                area = 3.4,
                type = "TOMATO",
                soilMoisture = 52.0,
                lastValidSoilMoisture = 52.0,
                soilTemperature = 23.0,
                lastValidSoilTemperature = 23.0,
                airTemperature = 27.0,
                lastValidAirTemperature = 27.0,
                airHumidity = 71.0,
                lastValidAirHumidity = 71.0,
                lux = 18000.0,
                lastValidLux = 18000.0,
                totalSensors = 2,
                activeSensors = 2,
                health = FieldHealth.HEALTHY,
                connectivity = FieldConnectivity.ONLINE,
                lifecycle = FieldLifecycle.ACTIVE
            ),
            points = listOf(
                FieldPoints(fieldId = 104, pointOrder = 0, latitude = 50.285050, longitude = 71.622350),
                FieldPoints(fieldId = 104, pointOrder = 1, latitude = 50.285350, longitude = 71.622650),
                FieldPoints(fieldId = 104, pointOrder = 2, latitude = 50.285200, longitude = 71.623000),
                FieldPoints(fieldId = 104, pointOrder = 3, latitude = 50.284850, longitude = 71.622950),
                FieldPoints(fieldId = 104, pointOrder = 4, latitude = 50.284750, longitude = 71.622550)
            )
        ),
        FieldWithPoints(
            field = FieldDetails(
                id = 105,
                farmId = 1,
                title = "Archived Field",
                area = 3.4,
                type = "ORANGE",
                soilMoisture = 52.0,
                lastValidSoilMoisture = 52.0,
                soilTemperature = 23.0,
                lastValidSoilTemperature = 23.0,
                airTemperature = 27.0,
                lastValidAirTemperature = 27.0,
                airHumidity = 71.0,
                lastValidAirHumidity = 71.0,
                lux = 18000.0,
                lastValidLux = 18000.0,
                totalSensors = 2,
                activeSensors = 2,
                health = FieldHealth.HEALTHY,
                connectivity = FieldConnectivity.OFFLINE,
                lifecycle = FieldLifecycle.ARCHIVED
            ),
            points = listOf(
                FieldPoints(
                    fieldId = 105,
                    pointOrder = 0,
                    latitude = 50.286900,
                    longitude = 71.624650
                ),
                FieldPoints(
                    fieldId = 105,
                    pointOrder = 1,
                    latitude = 50.287200,
                    longitude = 71.624950
                ),
                FieldPoints(
                    fieldId = 105,
                    pointOrder = 2,
                    latitude = 50.287050,
                    longitude = 71.625350
                ),
                FieldPoints(
                    fieldId = 105,
                    pointOrder = 3,
                    latitude = 50.286700,
                    longitude = 71.625250
                ),
                FieldPoints(
                    fieldId = 105,
                    pointOrder = 4,
                    latitude = 50.286600,
                    longitude = 71.624850
                )
            )
        )
    )
}