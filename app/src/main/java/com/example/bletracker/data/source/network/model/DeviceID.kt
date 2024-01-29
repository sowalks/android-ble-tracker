package com.example.bletracker.data.source.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceID (
    @SerialName("device_id") var deviceID : Int
)
