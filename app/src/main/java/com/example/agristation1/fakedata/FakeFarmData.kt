package com.example.agristation1.fakedata

import com.example.agristation1.data.farmDetails.FarmDetails
import java.time.Instant

object FakeFarmData {

    val farmDetails = FarmDetails(
        id = 1,
        farmName = "Green Valley Farm",
        lastUpdate = Instant.ofEpochSecond(Instant.now().epochSecond),
        activeSensors = 22,
        totalSensors = 27
    )

}