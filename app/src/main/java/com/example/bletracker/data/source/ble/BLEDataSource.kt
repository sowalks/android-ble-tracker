package com.example.bletracker.data.source.ble

import android.os.Environment
import android.util.Log
import android.view.View
import java.io.FileOutputStream

interface BluetoothLowEnergyDataSource
{

}
class BLEDataSource {




val parser
val beaconManager
val region
//UIActivity
val rangingObserver
//ForegroundService
val centralrangingObserver
    fun setupForegroundService
    fun setupBeaconScanning
}

val rangingObserver = Observer<Collection<Beacon>> { beacons ->
    Log.d(TAG, "Ranged: ${beacons.count()} beacons")
    if (BeaconManager.getInstanceForApplication(this).rangedRegions.size > 0) {
        beaconCountTextView.text = "Ranging enabled: ${beacons.count()} beacon(s) detected"
        beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            beacons
                .sortedBy { it.distance }
                .map { "id1:${it.id1}\n id2: ${it.id2}id3:${it.id3}  rssi: ${it.rssi}\nest. distance: ${it.distance} m" }.toTypedArray())
    }
}

fun rangingButtonTapped(view: View) {
    val beaconManager = BeaconManager.getInstanceForApplication(this)
    if (beaconManager.rangedRegions.size == 0) {
        beaconManager.startRangingBeacons(beaconReferenceApplication.region)
        rangingButton.text = "Stop Ranging"
        beaconCountTextView.text = "Ranging enabled -- awaiting first callback"
    }
    else {
        beaconManager.stopRangingBeacons(beaconReferenceApplication.region)
        rangingButton.text = "Start Ranging"
        beaconCountTextView.text = "Ranging disabled -- no beacons detected"
        beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("--"))
    }
}
//write to csv when the tags are detected
val centralRangingObserver = Observer<Collection<Beacon>> { beacons ->
    val rangeAgeMillis = System.currentTimeMillis() - (beacons.firstOrNull()?.lastCycleDetectionTimestamp ?: 0)
    if (rangeAgeMillis < 10000) {
        Log.d(MainActivity.TAG, "Ranged: ${beacons.count()} beacons")
        FileOutputStream( "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/log_e3_pixel_1ft_321397.csv",true ).writeCsv(beacons)
        for (beacon: Beacon in beacons) {
            Log.d(TAG, "$beacon about ${beacon.distance} meters away")
        }
    }
    else {
        Log.d(MainActivity.TAG, "Ignoring stale ranged beacons from $rangeAgeMillis millis ago")
    }
}

val centralRangingObserver = Observer<Collection<Beacon>> { beacons ->
    val rangeAgeMillis = System.currentTimeMillis() - (beacons.firstOrNull()?.lastCycleDetectionTimestamp ?: 0)
    if (rangeAgeMillis < 10000) {
        Log.d(MainActivity.TAG, "Ranged: ${beacons.count()} beacons")
        for (beacon: Beacon in beacons) {
            Log.d(TAG, "$beacon about ${beacon.distance} meters away")
        }
    }
    else {
        Log.d(MainActivity.TAG, "Ignoring stale ranged beacons from $rangeAgeMillis millis ago")
    }
}
