package com.example.bletracker.data.source.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DeviceID (
    @Serializable(with= UUIDSerializer::class)
    @SerialName("device_id") var deviceID : UUID
)
