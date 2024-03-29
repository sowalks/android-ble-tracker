package com.example.bletracker.fake

import com.example.bletracker.data.repository.LocalDeviceIDRepository
import com.example.bletracker.data.model.DeviceID

class FakeDeviceIDRepository() : LocalDeviceIDRepository {
    override suspend fun get(): DeviceID {
        return FakeDataSource.deviceID
    }

    override suspend fun set(deviceID: DeviceID) {
        TODO("Not yet implemented")
    }
}