package com.example.bletracker.fake

import com.example.bletracker.data.model.DeviceID
import com.example.bletracker.data.repository.LocalDeviceIDRepository

class FakeDeviceIDRepository() : LocalDeviceIDRepository {
    override suspend fun get(): DeviceID {
        return FakeDataSource.deviceID
    }

    override suspend fun set(deviceID: DeviceID) {
        return
    }
}