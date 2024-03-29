package com.example.bletracker.fake

import com.example.bletracker.data.utils.network.LocatorApiService
import com.example.bletracker.data.model.DeviceID
import com.example.bletracker.data.model.Entries
import com.example.bletracker.data.model.LogStatus
import com.example.bletracker.data.model.Status
import com.example.bletracker.data.source.network.model.Registrator


class FakeLocatorApiService : LocatorApiService {
    override suspend fun getDeviceID(): DeviceID {
        return FakeDataSource.deviceID
    }

    override suspend fun getLocations(deviceID: DeviceID): Entries {
        return FakeDataSource.locatorEntries
    }

    override suspend fun registerTag(register: Registrator): Status {
        return FakeDataSource.statusSuccess
    }

    override suspend fun submitLog(entries: Entries): LogStatus {
        return FakeDataSource.logStatusSuccess
    }

}