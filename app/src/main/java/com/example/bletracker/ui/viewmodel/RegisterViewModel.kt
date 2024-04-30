package com.example.bletracker.ui.viewmodel

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
import com.example.bletracker.BackgroundApplication
import com.example.bletracker.data.model.Entry
import com.example.bletracker.data.model.UpdateUiState
import com.example.bletracker.data.repository.LocationRepository
import com.example.bletracker.data.repository.NetworkRepository
import com.example.bletracker.data.repository.OwnedTagsRepository
import com.example.bletracker.data.utils.ble.BeaconRangingSmoother
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import retrofit2.HttpException
import java.io.IOException




class RegisterViewModel(val locatorRepository:NetworkRepository,
                        private val ownedTagsRepository: OwnedTagsRepository,
                        private val locationRepository: LocationRepository,
                        private val smoothingPeriod: Long = 10000) : ViewModel(){

// uiState is used to update screen, but private set to only be modified here
var registerUiState: UpdateUiState by mutableStateOf(UpdateUiState.Idle)
        private set
private val beaconSmoother : BeaconRangingSmoother =  BeaconRangingSmoother(smoothingPeriod)

fun registerTag(entry: Entry) {
        //regiter tag, in server, store in owned repo
        viewModelScope.launch{
                registerUiState = UpdateUiState.Loading
                Log.d(TAG, " Register Loading")
                registerUiState = try {
                        when (val status = locatorRepository.registerTag(tag=entry.tag,mode=true)) {
                            -2 -> { Log.d(TAG, "Already Registered")
                                    UpdateUiState.Error("Register Failed")}
                            -1 -> { Log.d(TAG, "Server failed Register")
                                    UpdateUiState.Error("Register Failed, Try again.")}
                            else -> { Log.d(TAG, "Success")
                                    // we need to add users' current location for recent tags
                                    //status will be tagID as it is not error status
                                    val pos  = locationRepository.addPosition()
                                    entry.position.latitude = pos.latitude
                                    entry.position.longitude = pos.longitude
                                    ownedTagsRepository.addTag(entry,status)
                                    UpdateUiState.Success(status)}
                        }
                }
                catch(e : IOException){
                        Log.d(TAG, " Register IO Error")
                        UpdateUiState.Error(e.toString())
                }
                catch(e : HttpException){
                        Log.d(TAG, " Register HTTP Error")
                        UpdateUiState.Error(e.message())
                }
        }

}
        fun userNotified(){
                registerUiState = UpdateUiState.Idle
        }
        fun smoothBeacons(beacons:Collection<Beacon>) : Collection<Beacon>
        {
                return beaconSmoother.add(beacons).visibleBeacons
        }
companion object{
        const val TAG = "RegisterViewModel"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
                initializer {
                        val application = (this[APPLICATION_KEY] as BackgroundApplication)
                        val locatorRepository = application.container.networkLocatorRepository
                        val ownedTagsRepository = application.container.ownedTagsRepository
                        val locationRepository = application.container.locationRepository
                        val smoothingPeriod = application.container.smoothingPeriod
                        RegisterViewModel(locatorRepository = locatorRepository,
                                ownedTagsRepository = ownedTagsRepository,
                                locationRepository=locationRepository,
                                smoothingPeriod=smoothingPeriod)
                }
        }
}
}
