package com.example.bletracker

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import android.content.Intent
import com.example.bletracker.BeaconReferenceApplication


class RegisterActivity : AppCompatActivity() {

        private lateinit var beaconListView: ListView
        private lateinit var beaconCountTextView: TextView
        private lateinit var rangingButton: Button
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
        beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
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
        beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("--"))
        }
        }

        companion object {
        val TAG = "MainActivity"
        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
        val PERMISSION_REQUEST_FINE_LOCATION = 3
        }

        }