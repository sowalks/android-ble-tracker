package com.example.bletracker.data.source.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Position (
    var longitude : Double,
    var latitude : Double
)