package uk.ac.cam.smw98.bletracker.fake

import uk.ac.cam.smw98.bletracker.data.model.DeviceID
import uk.ac.cam.smw98.bletracker.data.model.Entries
import uk.ac.cam.smw98.bletracker.data.model.LogStatus
import uk.ac.cam.smw98.bletracker.data.model.RegistrationFields
import uk.ac.cam.smw98.bletracker.data.model.SetModeBody
import uk.ac.cam.smw98.bletracker.data.model.Status
import uk.ac.cam.smw98.bletracker.data.datasource.LocatorApiService


class FakeLocatorApiService : LocatorApiService {
    override suspend fun getDeviceID(): DeviceID {
        return FakeDataSource.deviceID
    }

    override suspend fun getLocations(deviceID: DeviceID): Entries {
        return FakeDataSource.locatorEntries
    }

    override suspend fun setMode(setModeBody: SetModeBody): Status {
        return FakeDataSource.statusSuccess
    }

    override suspend fun submitLog(entries: Entries): LogStatus {
        return FakeDataSource.logStatusSuccess
    }

    override suspend fun registerTag(register: RegistrationFields): Status {
            return FakeDataSource.statusSuccess
    }

}