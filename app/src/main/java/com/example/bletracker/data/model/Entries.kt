package com.example.bletracker.data.model

import com.example.bletracker.data.utils.network.UUIDSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class Entry(
    // Give 0 value for the tag field not used.
    var time: LocalDateTime = LocalDateTime(1970,1,1,0,0,0,0),
    var tag: Tag =  Tag(),
    @SerialName("tag_id")
    var tagID: Int = 0,
    var distance: Double = 0.0,
    @SerialName("device_position")
    var position: Position
) {
    //deep copy by serialization, if fields change, it is easier/concise
    fun deepCopy() : Entry {
        return Json.decodeFromString(Json.encodeToString(serializer(),this))
    }
}

@Serializable
data class Entries (
  var entries : List<Entry>)


@Serializable
data class Tag (
    var major : UShort = 0U,
    var minor : UShort = 0U,
    @Serializable(with= UUIDSerializer::class)
    var uuid  : UUID = UUID(0,0)
)
@Serializable
data class Position (
    var longitude : Double,
    var latitude : Double
)
@Serializable
data class DeviceID (
    @Serializable(with= UUIDSerializer::class)
    @SerialName("device_id") var deviceID : UUID
)
@Serializable
data class Status(
    var status : Int
)
@Serializable
data class LogStatus(
    var status : List<Int>
)



