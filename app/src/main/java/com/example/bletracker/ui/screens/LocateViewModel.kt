package com.example.bletracker.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bletracker.MarsPhotosApplication
import com.example.bletracker.data.repository.LocatorRepository
import com.example.bletracker.data.source.network.model.Entries
import kotlinx.coroutines.launch
import retrofit2.HttpException
import android.util.Log
import java.io.IOException
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.bletracker.BeaconReferenceApplication


sealed interface   LocatorUiState {
    data class Success(val tags: Entries) : LocatorUiState
    data class Error(val msg: String) : LocatorUiState
    object Loading : LocatorUiState
}

class LocateViewModel(private val locatorRepository: LocatorRepository) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var locatorUiState: LocatorUiState by mutableStateOf(LocatorUiState.Loading)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getOwnedTags()
    }

    fun getOwnedTags() {
        viewModelScope.launch{
            locatorUiState = LocatorUiState.Loading
            locatorUiState = try {
               LocatorUiState.Success(locatorRepository.getLocations())}
            catch(e : IOException){
                Log.d(TAG, e.toString())
                LocatorUiState.Error("IO Error")
            }
            catch(e : HttpException){
                Log.d(TAG, e.message())
                LocatorUiState.Error("Http Error")
            }
        }
    }

    companion object {
        val TAG = "LocateViewModel"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BeaconReferenceApplication)
                val locatorRepository = application.container.locatorRepository
                MarsViewModel(locatorRepository = locatorRepository)
            }
        }
    }

}
