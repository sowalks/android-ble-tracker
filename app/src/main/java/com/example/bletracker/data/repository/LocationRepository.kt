package com.example.bletracker.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.bletracker.data.source.network.model.Entry
import com.example.bletracker.data.source.network.model.Position
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

interface LocationRepository {
    suspend fun addPosition(entry: Entry) : Entry
    suspend fun updateRecentLocation()
}
class LocationFusedRepository(private val locationClient: FusedLocationProviderClient,private val permissionManager: AppPermissionManager) : LocationRepository{
    private lateinit var recentLocation : Position

    override suspend fun addPosition(entry: Entry): Entry {
        if(permissionManager.hasAllPermissions) {
            if (recentLocation == null) {
                Log.d(TAG, "No recent location")
                updateRecentLocation()
            }
            entry.position = Position(recentLocation.longitude, recentLocation.latitude)
            Log.d(TAG, "Position set to ${recentLocation.latitude},${recentLocation.longitude}")
        }
        else {
            entry.position = Position(-1.0, -1.0)
            Log.d(TAG, "Permissions not Granted")
        }
            return entry
        }


    @SuppressLint("MissingPermission")
    override suspend fun updateRecentLocation() {
        //To get more accurate or fresher device location use this method
        if(permissionManager.hasAllPermissions){
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        val result = locationClient.getCurrentLocation(
            priority,
            CancellationTokenSource().token,
        ).await()

        result?.let { fetchedLocation ->
            Log.d(TAG,"Location updated")
            recentLocation = Position(
                fetchedLocation.longitude,
                fetchedLocation.latitude
            )
            return
        }
        }
        Log.d(TAG,"Update  Location Error")
        recentLocation = Position(-1.0,-1.0)
    }

    companion object{
        const val TAG = "LocationRepo"
    }
}
