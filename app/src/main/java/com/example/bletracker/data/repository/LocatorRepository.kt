package com.example.bletracker.data.repository

import com.example.bletracker.data.source.network.LocatorApiService
import com.example.bletracker.data.source.network.model.DeviceID
import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.Registrator
import com.example.bletracker.data.source.network.model.Tag
import kotlinx.coroutines.runBlocking


interface LocatorRepository {
    suspend fun getLocations() : Entries
    suspend fun submitLog(entries : Entries) : List<Int>
    suspend fun registerTag(tag : Tag, mode:Boolean) : Int
    suspend fun getDeviceID(): DeviceID
}

class NetworkLocatorRepository(
    private val locatorApiService: LocatorApiService,
    private val localDeviceIDRepository : LocalDeviceIDRepository
): LocatorRepository {

    // Must have a valid deviceID on initialization
    // Therefore it is blocking
    private val deviceID: DeviceID = runBlocking {
        var testID = localDeviceIDRepository.get()
        if(testID.deviceID < 0)
        {
            testID = locatorApiService.getDeviceID()
            localDeviceIDRepository.set(testID)
        }
        testID
    }
    override suspend fun getDeviceID(): DeviceID {
        return deviceID
    }

    override suspend fun getLocations(): Entries {
        return locatorApiService.getLocations(deviceID)
    }

    override suspend fun registerTag(tag : Tag, mode:Boolean): Int {
        return locatorApiService.registerTag(Registrator(tag,deviceID.deviceID,mode)).status
    }

    override suspend fun submitLog(entries: Entries): List<Int> {
        return locatorApiService.submitLog(entries).status
    }
}