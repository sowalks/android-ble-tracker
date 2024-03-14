package com.example.bletracker.data.source.network.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class Entry(
    // Give 0 value for the tag field not used.
    var time: LocalDateTime,
    var tag: Tag =  Tag(0U,0U,UUID(0,0)),
    @SerialName("tag_id")
    var tagID : Int = 0,
    var distance: Double,
    @SerialName("device_position")
    var position : Position
) {
    //deep copy by serialization, if fields change, it is easier and more concise(esp. w cbor))
    fun deepCopy() : Entry {
        return Json.decodeFromString(Json.encodeToString(serializer(),this))
    }
}

@Serializable
data class Entries (
  var entries : List<Entry>)


@Serializable
data class Status(
    var status : Int
)
@Serializable
data class LogStatus(
    var status : List<Int>
)


