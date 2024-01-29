package com.example.bletracker.data.source.network.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Tag (
    var major : UShort,
    var minor : UShort,
    @Serializable(with= UUIDSerializer::class)
    var uuid  : UUID
)