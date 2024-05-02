package uk.ac.cam.smw98.bletracker.ui.viewmodel
/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified from MarsPhotos CodeLab MarsViewModel.kt.
 */

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
import uk.ac.cam.smw98.bletracker.BackgroundApplication
import uk.ac.cam.smw98.bletracker.data.model.Entries
import uk.ac.cam.smw98.bletracker.data.model.UpdateUiState
import uk.ac.cam.smw98.bletracker.data.repository.NetworkRepository
import uk.ac.cam.smw98.bletracker.data.repository.OwnedTagsRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException


sealed interface   LocatorUiState {
    object Success : LocatorUiState
    data class Error(val msg: String) : LocatorUiState
    object Loading : LocatorUiState
}

class LocateViewModel(private val locatorRepository: NetworkRepository,
private val ownedTagsRepository: OwnedTagsRepository
) : ViewModel() {

    var locatorUiState: LocatorUiState by mutableStateOf(LocatorUiState.Loading)
        private set

    var setModeState: UpdateUiState by mutableStateOf(UpdateUiState.Idle)
        private set

    var tags: Entries by mutableStateOf(Entries(emptyList()))

    init {
        getOwnedTags()
    }

    fun getOwnedTags() {
        viewModelScope.launch {
            locatorUiState = LocatorUiState.Loading
            locatorUiState = try {
                tags = ownedTagsRepository.getRecentEntries(locatorRepository.getLocations())
                LocatorUiState.Success
            }
            catch (e: IOException) {
                Log.d(TAG, e.toString())
                LocatorUiState.Error("IO Error")
            } catch (e: HttpException) {
                Log.d(TAG, e.message())
                LocatorUiState.Error("Http Error")
            } catch(e: SocketTimeoutException){
                Log.d(TAG, e.toString())
                LocatorUiState.Error("Server timed out, Try again")
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
            }catch(e: SocketTimeoutException){
               Log.d(TAG, e.toString())
               UpdateUiState.Error("Server timed out, Try again")
           }
        }
    }


    //Event when a  user has already been notified, prevents repeated notification
    //when switching tabs, by defaulting to idle
    fun userNotified(){
        setModeState = UpdateUiState.Idle
    }

    companion object {
        const val TAG = "LocateViewModel"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as uk.ac.cam.smw98.bletracker.BackgroundApplication)
                val locatorRepository = application.container.networkLocatorRepository
                val localOwnedRepository = application.container.ownedTagsRepository
                LocateViewModel(locatorRepository = locatorRepository, ownedTagsRepository = localOwnedRepository)
            }
        }
    }
}




