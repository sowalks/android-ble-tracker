package com.example.bletracker.fake

import com.example.bletracker.data.model.DeviceID
import com.example.bletracker.data.model.Entries
import com.example.bletracker.data.model.LogStatus
import com.example.bletracker.data.model.RegistrationFields
import com.example.bletracker.data.model.SetModeBody
import com.example.bletracker.data.model.Status
import com.example.bletracker.data.utils.network.LocatorApiService


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