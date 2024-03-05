package com.example.bletracker.data.source.network.model

sealed interface  UpdateUiState {
    data class Success(val status: Int) : UpdateUiState
    data class Error(val msg: String) : UpdateUiState
    object Idle : UpdateUiState
    object Loading: UpdateUiState
}