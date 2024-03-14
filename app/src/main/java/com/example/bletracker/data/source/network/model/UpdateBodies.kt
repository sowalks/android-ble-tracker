package com.example.bletracker.data.source.network.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class RegistrationFields(
    var tag      : Tag,
    @SerialName("device_id") var deviceId : Int,
    var mode     : Boolean
)

@Serializable
data class SetModeBody(
    @SerialName("device_id")  var deviceID : Int,
    @SerialName("tag_id") var tagID: Int,
    var mode: Boolean
)