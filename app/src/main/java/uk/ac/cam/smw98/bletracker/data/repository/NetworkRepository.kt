package uk.ac.cam.smw98.bletracker.data.repository

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
 * Modified from MarsPhotos  Code Lab, MarsPhotosRepository.kt
 */

import android.util.Log
import uk.ac.cam.smw98.bletracker.data.datasource.TrackerApiService
import uk.ac.cam.smw98.bletracker.data.model.DeviceID
import uk.ac.cam.smw98.bletracker.data.model.Entries
import uk.ac.cam.smw98.bletracker.data.model.RegistrationFields
import uk.ac.cam.smw98.bletracker.data.model.SetModeBody
import uk.ac.cam.smw98.bletracker.data.model.Tag


interface NetworkRepository {
    suspend fun getLocations() : Entries
    suspend fun submitLog(entries : Entries) : List<Int>
    suspend fun registerTag(tag : Tag, mode:Boolean) : Int
    suspend fun setMode(tagID: Int,mode:Boolean) : Int
}

class NetworkLocatorRepository(
    private val trackerApiService:TrackerApiService,
    private val localDeviceIDRepository : LocalDeviceIDRepository
): NetworkRepository {


    private suspend fun getDeviceID(): DeviceID {
        return try {
            //try get deviceid out of local storage
            localDeviceIDRepository.get()
        } catch(e: IllegalArgumentException){
            //first use of app - retrieve new ID from server
            // then store locally
            val testID =trackerApiService.getDeviceID()
            localDeviceIDRepository.set(testID)
            Log.d(TAG,"DeviceID  = $testID")
            testID
        }
    }

    override suspend fun getLocations(): Entries {
        return trackerApiService.getLocations(getDeviceID().deviceID.toString())
    }

    override suspend fun registerTag(tag : Tag, mode:Boolean): Int {
        return trackerApiService.registerTag(RegistrationFields(tag,getDeviceID().deviceID,mode)).status
    }

    override suspend fun setMode(tagID: Int,mode: Boolean): Int
    {
        return trackerApiService.setMode(tagID, SetModeBody(deviceID = getDeviceID().deviceID,mode=mode)).status
    }

    override suspend fun submitLog(entries: Entries): List<Int> {
        return trackerApiService.submitLog(entries).status
    }
    companion object{
        const val TAG = "NetworkRepo"
    }
}