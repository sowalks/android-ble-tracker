package uk.ac.cam.smw98.bletracker.data.model

sealed interface  UpdateUiState {
    data class Success(val status: Int) : UpdateUiState
    data class Error(val msg: String) : UpdateUiState
    object Idle : UpdateUiState
    object Loading: UpdateUiState
}