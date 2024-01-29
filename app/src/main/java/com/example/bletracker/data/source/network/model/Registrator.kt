package com.example.bletracker.data.source.network.model
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class Registrator (
    var tag      : Tag,
    @SerialName("device_id") var deviceId : Int,
    var mode     : Boolean
)