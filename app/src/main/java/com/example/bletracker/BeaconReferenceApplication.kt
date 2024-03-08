package com.example.bletracker

import android.app.Application
import android.util.Log
import com.example.bletracker.data.AppContainer
import com.example.bletracker.data.DefaultAppContainer
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BeaconReferenceApplication : Application() {
    lateinit var container: AppContainer
    private val executor: ExecutorService = Executors.newFixedThreadPool(1)

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
        //Separate thread required to execute periodic requests
        // more regularly than ~15 minutes w/ Android killing it
        //runblocking as we want this to be in order
        executor.execute(Runnable{ runBlocking {
            while (true) {
                val beacons = container.logRepository.consumeLog()
                if (beacons.entries.isNotEmpty()) {
                    val success =
                        try {
                            container.networkLocatorRepository.submitLog(beacons)
                        } catch (e: IOException) {
                            Log.d(TAG, e.toString())
                            listOf(-2)
                        } catch (e: HttpException) {
                            Log.d(TAG, e.message())
                            listOf(-2)
                        }
                    if (success.contains(-1)) {
                        Log.d(TAG, "Error in submitting log of ${success.size} beacons")
                    } else if (success.size == 1 && success[0] == -2) {
                        Log.d(TAG, "Connection Error on ${beacons.entries.size}")
                    }
                }
                Log.d(TAG, "I will log this line every ${container.loggingPeriod} forever")
                container.locationRepository.updateRecentLocation()
                Thread.sleep(container.loggingPeriod)
                // FileOutputStream( "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/log=_10sec.csv",true ).writeCsv(beacons)
            }
            }
        })

        container.bleHelper.setupBeaconScanning()
    }

    companion object {
        const val TAG = "BeaconReference"
    }

}
//TODO CHANGE RUNBLOCKING COROUTINE SCOPE