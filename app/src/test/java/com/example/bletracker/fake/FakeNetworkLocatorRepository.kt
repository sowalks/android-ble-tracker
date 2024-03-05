package com.example.bletracker.fake

import com.example.bletracker.data.repository.LocatorRepository
import com.example.bletracker.data.source.network.model.DeviceID
import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.Tag


class FakeNetworkLocatorRepository(): LocatorRepository {
    override suspend fun getLocations(): Entries {
        return FakeDataSource.locatorEntries
    }

    override suspend fun getDeviceID(): DeviceID {
        return FakeDataSource.deviceID
    }


    override suspend fun submitLog(entries: Entries): List<Int> {
        return FakeDataSource.logStatusSuccess.status
    }

    override suspend fun registerTag(tag: Tag, mode: Boolean): Int {
        return FakeDataSource.statusSuccess.status
    }
}