package com.example.bletracker.fake

import com.example.bletracker.data.repository.NetworkLocatorRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NetworkLocatorRepositoryTest {
    @Test
    fun networkLocatorRepository_getLocations_verifyPhotoList() = runTest {

            val repository = NetworkLocatorRepository(
                 locatorApiService = FakeLocatorApiService(),
                 localDeviceIDRepository = FakeDeviceIDRepository()
            )
            assertEquals(FakeDataSource.deviceID, repository.getDeviceID())
            assertEquals(FakeDataSource.locatorEntries, repository.getLocations())
            assertEquals(FakeDataSource.logStatusSuccess.status, repository.submitLog(FakeDataSource.logEntries))
            assertEquals(FakeDataSource.statusSuccess.status, repository.registerTag(FakeDataSource.registrator.tag,FakeDataSource.registrator.mode))
    }
}