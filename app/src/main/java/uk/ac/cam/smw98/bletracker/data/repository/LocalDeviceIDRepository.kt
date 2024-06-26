package uk.ac.cam.smw98.bletracker.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import uk.ac.cam.smw98.bletracker.data.model.DeviceID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.UUID


interface LocalDeviceIDRepository {
    suspend fun get() : DeviceID
    suspend fun set(deviceID: DeviceID)
}


class DefaultDeviceIDRepository(
    private val dataStore: DataStore<Preferences>
) :  LocalDeviceIDRepository {


    private val deviceIDFlow: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading deviceID.", it)
                //emptyPreferences will log my default : -1
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[DEVICE_ID] ?: "-1"
        }

    //Reads integer deviceID from datastore
    override suspend fun get(): DeviceID {
        // gets DeviceID from datastore as string
        val deviceIDString = deviceIDFlow.first()
        return DeviceID(UUID.fromString(deviceIDString))
    }

    //saves DeviceID integer to datastore as a preference
    override suspend fun set(deviceID: DeviceID) {
        dataStore.edit { preferences ->
            preferences[DEVICE_ID] = deviceID.deviceID.toString()
        }
    }

    //holds consts for getting DeviceID and logging w TAG
    companion object PreferenceKeys {
        val DEVICE_ID = stringPreferencesKey("device_id")
        const val TAG = "DeviceIDRepo"
    }

}

