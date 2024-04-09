package com.example.bletracker.data.repository

import com.example.bletracker.fake.FakeDataSource
import org.junit.After
import org.junit.Before
import org.junit.Test

class DefaultDeviceIDRepositoryTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun get() {
    }

    @Test
    fun set() {
    }
}
package com.example.bletracker.fake
import android.content.Context

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.bletracker.data.repository.DefaultDeviceIDRepository
import com.example.bletracker.data.repository.LocalDeviceIDRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.createTestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith


private const val TEST_DATASTORE_NAME: String = "test_device"

//BAD LEAVE FOR NOW

class DeviceIDRepositoryTest {
    private val testContext: Context =
        InstrumentationRegistry.getInstrumentation().targetContext
    private val testCoroutineDispatcher: TestCoroutineDispatcher =
        TestCoroutineDispatcher()
    private val testCoroutineScope =
        createTestCoroutineScope(testCoroutineDispatcher + Job())
    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile =
            { testContext.preferencesDataStoreFile(TEST_DATASTORE_NAME) }
        )
    private val repository: LocalDeviceIDRepository =
        DefaultDeviceIDRepository(testDataStore)

    @Test
    fun repository_testFetchDeviceID() {
        runTest {
            val res = repository.get()
            assertEquals(res.deviceID, -1)

        }
    }

    @Test
    fun repository_testWriteDeviceID() {
        runTest {
            repository.set(FakeDataSource.deviceID)
            assertEquals(repository.get().deviceID, FakeDataSource.deviceID.deviceID)
        }
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        runTest {
            testDataStore.edit { it.clear() }
        }
    }
}




