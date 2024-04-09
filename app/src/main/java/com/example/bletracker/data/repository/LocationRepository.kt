package com.example.bletracker.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.bletracker.data.model.Entry
import com.example.bletracker.data.model.Position
import com.example.bletracker.data.utils.AppPermissionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

interface LocationRepository {
    suspend fun addPosition(entry: Entry) : Entry
    suspend fun updateRecentLocation()
}
class LocationFusedRepository(private val locationClient: FusedLocationProviderClient,private val permissionManager: AppPermissionManager) : LocationRepository{
    private var recentLocation : Position = Position(-200.0,-200.0)
    override suspend fun addPosition(entry: Entry): Entry {
        if(permissionManager.hasAllPermissions()) {
            entry.position.longitude = recentLocation.longitude
            entry.position.latitude = recentLocation.latitude
            Log.d(TAG, "Position set to ${recentLocation.latitude},${recentLocation.longitude}")
        }
        else {
            entry.position.longitude =-200.0
            entry.position.latitude = -200.0
            Log.d(TAG, "Permissions not Granted")
        }
            return entry
        }


    @SuppressLint("MissingPermission")
    override suspend fun updateRecentLocation() {
        //get more recent/updated location
        if(permissionManager.hasAllPermissions()){
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
        recentLocation = Position(-200.0,-200.0)
    }

    companion object{
        const val TAG = "LocationRepo"
    }
}
