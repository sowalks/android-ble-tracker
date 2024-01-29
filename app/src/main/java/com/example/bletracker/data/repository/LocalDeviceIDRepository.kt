package com.example.bletracker.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.bletracker.data.source.network.model.DeviceID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.io.IOException




interface LocalDeviceIDRepository {
    suspend fun get() : DeviceID
    suspend fun saveDeviceID(deviceID: DeviceID)
}


class DefaultDeviceIDRepository(
    private val dataStore: DataStore<Preferences>
) :  LocalDeviceIDRepository{

    //Reads integer deviceID from datastore
    private val deviceIDFlow: Flow<Int> = dataStore.data
        .catch{
            if(it is IOException) {
            Log.e(TAG, "Error reading deviceID.", it)
            //emptyPreferences will log my default : -1
            emit(emptyPreferences())
        } else {
            throw  it
        }
        }
        .map{
            preferences ->
            preferences[DEVICE_ID]  ?: -1
    }

    //holds consts for getting DeviceID and logging w TAG
    companion object PreferenceKeys {
        val DEVICE_ID = intPreferencesKey("device_id")
        const val TAG = "DeviceIDRepo"
    }

    override suspend fun get(): DeviceID {
        // gets DeviceID from datastore as int
        val deviceIDInt = dataStore.data.first().toPreferences()[DEVICE_ID] ?:-1
        return DeviceID(deviceIDInt)
    }

   //saves DeviceID integer to datastore as a preference
    override suspend fun saveDeviceID(deviceID : DeviceID) {
        dataStore.edit {preferences ->
            preferences[DEVICE_ID] = deviceID.deviceID
        }
    }
}