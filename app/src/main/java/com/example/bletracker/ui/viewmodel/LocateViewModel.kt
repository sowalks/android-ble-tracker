package com.example.bletracker.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bletracker.BackgroundApplication
import com.example.bletracker.data.model.Entries
import com.example.bletracker.data.model.UpdateUiState
import com.example.bletracker.data.repository.NetworkRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


sealed interface   LocatorUiState {
    object Success : LocatorUiState
    data class Error(val msg: String) : LocatorUiState
    object Loading : LocatorUiState
}

class LocateViewModel(private val locatorRepository: NetworkRepository) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var locatorUiState: LocatorUiState by mutableStateOf(LocatorUiState.Loading)
        private set

    var setModeState: UpdateUiState by mutableStateOf(UpdateUiState.Idle)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    var tags: Entries by mutableStateOf(Entries(emptyList()))

    init {
        getOwnedTags()
    }

    fun getOwnedTags() {
        viewModelScope.launch {
            locatorUiState = LocatorUiState.Loading
            locatorUiState = try {
                tags = locatorRepository.getLocations()
                LocatorUiState.Success
            }
            catch (e: IOException) {
                Log.d(TAG, e.toString())
                LocatorUiState.Error("IO Error")
            } catch (e: HttpException) {
                Log.d(TAG, e.message())
                LocatorUiState.Error("Http Error")
            }
        }
    }

    fun setTagMode(tagID: Int,mode:Boolean) {
        viewModelScope.launch {
           setModeState = try {
               UpdateUiState.Success(locatorRepository.setMode(tagID, mode = mode))
            }
            catch (e: IOException) {
                Log.d(TAG, e.toString())
                UpdateUiState.Error("Set Mode IO Error")
            } catch (e: HttpException) {
                Log.d(TAG, e.message())
                UpdateUiState.Error("Set Mode Http Error")
            }
        }
    }

    fun userNotified(){
        setModeState = UpdateUiState.Idle
    }

    companion object {
        const val TAG = "LocateViewModel"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BackgroundApplication)
                val locatorRepository = application.container.networkLocatorRepository
                LocateViewModel(locatorRepository = locatorRepository)
            }
        }
    }
}




