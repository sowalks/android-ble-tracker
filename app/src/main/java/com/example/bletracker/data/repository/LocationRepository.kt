package com.example.bletracker.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.bletracker.data.model.Position
import com.example.bletracker.data.utils.AppPermissionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

interface LocationRepository {
    fun addPosition() : Position
    suspend fun updateRecentLocation()
}
class LocationFusedRepository(private val locationClient: FusedLocationProviderClient,private val permissionManager: AppPermissionManager) : LocationRepository{
    private var recentLocation : Position = Position(-200.0,-200.0)
    override fun addPosition(): Position {
        return Position(recentLocation.longitude,recentLocation.latitude)
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
        Log.d(TAG,"Permissions not Granted")
        recentLocation = Position(-200.0,-200.0)
    }

    companion object{
        const val TAG = "LocationRepo"
    }
}
