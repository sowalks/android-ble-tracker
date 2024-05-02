package uk.ac.cam.smw98.bletracker.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class RegistrationFields(
    var tag      : Tag,
    @Serializable(with= UUIDSerializer::class)
    @SerialName("device_id") var deviceId : UUID,
    var mode     : Boolean
)

@Serializable
data class SetModeBody(
    @Serializable(with= UUIDSerializer::class)
    @SerialName("device_id")  var deviceID : UUID,
    var mode: Boolean
)