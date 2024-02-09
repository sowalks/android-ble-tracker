package com.example.bletracker.ui.screens

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bletracker.BeaconReferenceApplication
import com.example.bletracker.BeaconScanPermissionsActivity
import com.example.bletracker.MarsPhotosApplication
import com.example.bletracker.data.repository.LocatorRepository
import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.Entry
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager

sealed interface  RegisterUiState {
        data class BLESuccess(val tags: Entries = Entries(listOf<Entry>())) : RegisterUiState
        data class Error(val msg: String) : RegisterUiState
}

private lateinit var beaconReferenceApplication: BeaconReferenceApplication


override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beaconReferenceApplication = application as BeaconReferenceApplication

        // Set up a Live Data observer for beacon data
        val regionViewModel = BeaconManager.getInstanceForApplication(this)
                .getRegionViewModel(beaconReferenceApplication.container.region)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observe(this, rangingObserver)
}

override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
}
override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()

        if (!BeaconScanPermissionsActivity.allPermissionsGranted(this,
                        true)) {
                val intent = Intent(this, BeaconScanPermissionsActivity::class.java)
                intent.putExtra("backgroundAccessRequested", true)
                startActivity(intent)
        }
        else {
                // All permissions are granted now.  In the case where we are configured
                // to use a foreground service, we will not have been able to start scanning until
                // after permissions are granted.  So we will do so here.
                if (BeaconManager.getInstanceForApplication(this).rangedRegions.isEmpty()) {
                        (application as BeaconReferenceApplication).setupBeaconScanning()
                }
        }
}



val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        if (BeaconManager.getInstanceForApplication(this).rangedRegions.isNotEmpty()) {
                beaconCountTextView.text = "Ranging enabled: ${beacons.count()} beacon(s) detected"
                beaconListView.adapter = ArrayAdapter(this, R.layout.simple_list_item_1,
                        beacons
                                .sortedBy { it.distance }
                                .map { "${it.id1}\nid2: ${it.id2} id3:  rssi: ${it.rssi}\nest. distance: ${it.distance} m" }.toTypedArray())
        }
}

fun rangingButtonTapped(view: View) {
        val beaconManager = BeaconManager.getInstanceForApplication(this)
        if (beaconManager.rangedRegions.isEmpty()) {
                beaconManager.startRangingBeacons(beaconReferenceApplication.container.region)
                rangingButton.text = "Stop Ranging"
                beaconCountTextView.text = "Ranging enabled -- awaiting first callback"
        }
        else {
                beaconManager.stopRangingBeacons(beaconReferenceApplication.container.region)
                rangingButton.text = "Start Ranging"
                beaconCountTextView.text = "Ranging disabled -- no beacons detected"
                beaconListView.adapter = ArrayAdapter(this, R.layout.simple_list_item_1, arrayOf("--"))
        }
}

companion object {
        val TAG = "MainActivity"
}


import retrofit2.HttpException
import java.io.IOException

sealed interface MarsUiState {
        data class Success(val photos: String) : MarsUiState
        data class Error(val msg: String) : MarsUiState
        object Loading : MarsUiState
}



class MarsViewModel(private val locatorRepository: LocatorRepository) : ViewModel() {
        /** The mutable State that stores the status of the most recent request */
        var marsUiState: MarsUiState by mutableStateOf(MarsUiState.Loading)
                private set

        /**
         * Call getMarsPhotos() on init so we can display status immediately.
         */
        init {
                getMarsPhotos()
        }

        /**
         * Gets Mars photos information from the Mars API Retrofit service and updates the
         * [MarsPhoto] [List] [MutableList].
         */
        fun getMarsPhotos() {
                viewModelScope.launch{
                        marsUiState = MarsUiState.Loading
                        marsUiState = try {
                                MarsUiState.Success("Success: ${locatorRepository.getLocations().entries.size} Mars " +
                                        "photos retrieved")}
                        catch(e : IOException){
                                MarsUiState.Error(e.toString())
                        }
                        catch(e : HttpException){
                                MarsUiState.Error(e.message())
                        }
                }
        }

        companion object {
                val Factory: ViewModelProvider.Factory = viewModelFactory {
                        initializer {
                                val application = (this[APPLICATION_KEY] as MarsPhotosApplication)
                                val locatorRepository = application.container.locatorRepository
                                MarsViewModel(locatorRepository = locatorRepository)
                        }
                }
        }
}
