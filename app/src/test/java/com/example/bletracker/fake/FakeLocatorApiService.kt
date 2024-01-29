package com.example.bletracker.fake

import com.example.bletracker.data.source.network.LocatorApiService
import com.example.bletracker.data.source.network.model.DeviceID
import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.LogStatus
import com.example.bletracker.data.source.network.model.RegisterStatus
import com.example.bletracker.data.source.network.model.Registrator


class FakeLocatorApiService : LocatorApiService {
    override suspend fun getDeviceID(): DeviceID {
        return FakeDataSource.deviceID
    }

    override suspend fun getLocations(deviceID: DeviceID): Entries {
        return FakeDataSource.locatorEntries
    }

    override suspend fun registerTag(register: Registrator): RegisterStatus {
        return FakeDataSource.registerStatusSuccess
    }

    override suspend fun submitLog(entries: Entries): LogStatus {
        return FakeDataSource.logStatusSuccess
    }

}