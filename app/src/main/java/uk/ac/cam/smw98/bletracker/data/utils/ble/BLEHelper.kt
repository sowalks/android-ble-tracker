package uk.ac.cam.smw98.bletracker.data.utils.ble

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager


import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import uk.ac.cam.smw98.bletracker.R
import uk.ac.cam.smw98.bletracker.data.repository.LocationRepository
import uk.ac.cam.smw98.bletracker.data.repository.LogRepository
import kotlinx.coroutines.runBlocking
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

interface BLEBeaconHelper{
    fun setupBeaconScanning()
    fun setupForegroundService()
}

class BLEHelper(private val context: Context,
                private val logRepository: LogRepository,
                private val locationRepository: LocationRepository,
                private val region: Region,
                private val scanPeriod: Long = 1100L,
                private val betweenScanPeriod: Long = 0,
                smoothingPeriod: Long = 10000L) : BLEBeaconHelper {

    override fun setupBeaconScanning() {
        val beaconManager = BeaconManager.getInstanceForApplication(context)
        beaconManager.beaconParsers.clear()

        //Set iBeacon Layout
        val parser = BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        parser.setHardwareAssistManufacturerCodes(arrayOf(0x004c).toIntArray())
        beaconManager.beaconParsers.add(
            parser
        )
        //Foreground service required to scan more frequently than every 15 mins in Android 8+
        try {
            setupForegroundService()
            beaconManager.setEnableScheduledScanJobs(false)
            beaconManager.backgroundBetweenScanPeriod = betweenScanPeriod
            beaconManager.backgroundScanPeriod = scanPeriod

        } catch (e: SecurityException) {
            Log.d(
                TAG,
                "Not setting up foreground service scanning until location permission granted by user"
            )
            return
        } catch (e: RuntimeException) {
            Log.d(TAG, "Foreground Runtime error")
        }

        beaconManager.startRangingBeacons(region)
        // These two lines set up a Live Data observer so this Activity can get beacon data from the Application class
        val regionViewModel =
            BeaconManager.getInstanceForApplication(context).getRegionViewModel(region)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observeForever(centralRangingObserver)

    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

   // if need to smooth logs private val beaconSmoother: BeaconRangingSmoother = BeaconRangingSmoother(smoothingPeriod)

    override fun setupForegroundService() {
        val builder = Notification.Builder(context, "BeaconReferenceApp")
        builder.setSmallIcon(R.drawable.ic_launcher_background)
        builder.setContentTitle("Scanning for Beacons")
        val channel = NotificationChannel(
            "beacon-ref-notification-id",
            "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "My Notification Channel Description"
        notificationManager.createNotificationChannel(channel)
        builder.setChannelId(channel.id)
        Log.d(TAG, "Calling enableForegroundServiceScanning")
        BeaconManager.getInstanceForApplication(context)
            .enableForegroundServiceScanning(builder.build(), 456)
        Log.d(TAG, "Back from  enableForegroundServiceScanning")
    }

    private val centralRangingObserver = Observer<Collection<Beacon>> {  beacons ->
            val rangeAgeMillis =
                System.currentTimeMillis() - (beacons.firstOrNull()?.lastCycleDetectionTimestamp
                    ?: 0)
            if (rangeAgeMillis < scanPeriod) {
                Log.d(TAG, "Ranged: ${beacons.count()} beacons")
                //runblocking, as we want to block background thread before continuing
                runBlocking {
                    //Turn beacons to entries, add location and log them
                    val currentPosition = locationRepository.addPosition()
                    logRepository.appendLog(beacons.toListEntry().map{
                        it.position.longitude = currentPosition.longitude
                        it.position.latitude = currentPosition.latitude
                        it
                    },)
                }
            } else {
                //Log.d(TAG, "Ignoring stale ranged beacons from $rangeAgeMillis millis ago")
            }
}

    companion object {
        const val TAG = "BLEServiceHelper"
    }
//TODO: SET longScanForcingEnabled ON TO STOP ISSUE IN FIRST LOG
}

