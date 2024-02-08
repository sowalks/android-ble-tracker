package com.example.bletracker.ui.screens

import com.example.bletracker.data.source.network.model.Entries

sealed interface   LocatorUiState {
    data class Success(val tags: Entries) : LocatorUiState
    data class Error(val msg: String) : LocatorUiState
    object Loading : LocatorUiState
}