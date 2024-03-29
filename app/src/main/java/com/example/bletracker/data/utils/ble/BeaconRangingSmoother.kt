package com.example.bletracker.data.utils.ble

//TODO: BR

import org.altbeacon.beacon.Beacon

/*
 * This class is used to smooth out the beacon ranging data to prevent periodic dropouts.  By
 * default, it will retain beacons in the list if detected in the past 10 seconds, but you can
 * adjust this with the smoothingWindowMillis property.
 *
 * Changed only to ensure no duplicate beacons + they are sorted so the list positions do not switch
 */
class BeaconRangingSmoother(private val smoothingWindowMillis: Long = 10000){
    private var beacons: ArrayList<Beacon> = ArrayList()
    val visibleBeacons: List<Beacon>
        get() {
            val visible = ArrayList<Beacon>()
            for (beacon in beacons) {
                if (System.currentTimeMillis() - beacon.lastCycleDetectionTimestamp < smoothingWindowMillis  && !visible.contains(beacon)) {
                    visible.add(beacon)
                }
            }
            return visible.sortedWith( compareBy({ it.id1},{it.id2},{it.id3}) )
        }
    fun add(detectedBeacons: Collection<Beacon>): BeaconRangingSmoother {
        for (beacon in detectedBeacons) {
            beacon.lastCycleDetectionTimestamp = System.currentTimeMillis()
            beacons.add(beacon)
        }
        return this
    }
    companion object {
        const val TAG = "BeaconRangingSmoother"
    }
}