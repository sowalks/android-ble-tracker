package uk.ac.cam.smw98.bletracker.fake

import uk.ac.cam.smw98.bletracker.data.model.DeviceID
import uk.ac.cam.smw98.bletracker.data.repository.LocalDeviceIDRepository

class FakeDeviceIDRepository() : LocalDeviceIDRepository {
    override suspend fun get(): DeviceID {
        return FakeDataSource.deviceID
    }

    override suspend fun set(deviceID: DeviceID) {
        return
    }
}