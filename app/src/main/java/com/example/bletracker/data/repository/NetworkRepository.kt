package com.example.bletracker.data.repository

import android.util.Log
import com.example.bletracker.data.source.network.LocatorApiService
import com.example.bletracker.data.source.network.model.DeviceID
import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.RegistrationFields
import com.example.bletracker.data.source.network.model.SetModeBody
import com.example.bletracker.data.source.network.model.Tag


interface NetworkRepository {
    suspend fun getLocations() : Entries
    suspend fun submitLog(entries : Entries) : List<Int>
    suspend fun registerTag(tag : Tag,mode:Boolean) : Int
    suspend fun setMode(tagID: Int,mode:Boolean) : Int
}

class NetworkLocatorRepository(
    private val locatorApiService: LocatorApiService,
    private val localDeviceIDRepository : LocalDeviceIDRepository
): NetworkRepository {


    private suspend fun getDeviceID(): DeviceID {
            var testID = localDeviceIDRepository.get()
            if(testID.deviceID < 0)
            {
                testID = locatorApiService.getDeviceID()
                localDeviceIDRepository.set(testID)
            }
        Log.d(TAG,"DeviceID  = $testID")
        return  testID
    }

    override suspend fun getLocations(): Entries {
        return locatorApiService.getLocations(getDeviceID())
    }

    override suspend fun registerTag(tag : Tag,mode:Boolean): Int {
        return locatorApiService.registerTag(RegistrationFields(tag,getDeviceID().deviceID,mode)).status
    }

    override suspend fun setMode(tagID: Int,mode: Boolean): Int
    {
        return locatorApiService.setMode(SetModeBody(tagID=tagID, deviceID = getDeviceID().deviceID,mode=mode)).status
    }

    override suspend fun submitLog(entries: Entries): List<Int> {
        return locatorApiService.submitLog(entries).status
    }
    companion object{
        const val TAG = "LocatorRepo"
    }
}