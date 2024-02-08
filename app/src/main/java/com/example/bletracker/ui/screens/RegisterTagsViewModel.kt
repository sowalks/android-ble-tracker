package com.example.bletracker.ui.screens

import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.Entry

sealed interface  RegisterUiState {
        data class BLESuccess(val tags: Entries = Entries(listOf<Entry>())) : RegisterUiState
        data class Error(val msg: String) : RegisterUiState
}

