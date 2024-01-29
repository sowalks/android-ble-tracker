package com.example.bletracker.fake

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.core.app.ApplicationProvider
import com.example.bletracker.data.dataStore
import com.example.bletracker.data.repository.DefaultDeviceIDRepository
import com.example.bletracker.data.source.network.model.DeviceID
import com.example.bletracker.rules.TestDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


//TODO: BAD
val testContext = ApplicationProvider.getApplicationContext<Context>()
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "device")
@ExperimentalCoroutinesApi
class DeviceIDRepositoryTest{

        @get:Rule
    val testDispatcher = TestDispatcherRule()
    @Test
    fun defaultDeviceIDRepository_verify_get() = runTest {

            val repository = DefaultDeviceIDRepository(
                testContext.dataStore
            )
            repository.saveDeviceID(DeviceID(4))
            assertEquals(FakeDataSource.deviceID, repository.get())


    }
}