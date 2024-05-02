package uk.ac.cam.smw98.bletracker

import android.app.Application
import android.util.Log
import uk.ac.cam.smw98.bletracker.data.utils.AppContainer
import uk.ac.cam.smw98.bletracker.data.utils.DefaultAppContainer
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class BackgroundApplication : Application() {
    lateinit var container: AppContainer
    private val executor: ExecutorService = Executors.newFixedThreadPool(1)

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
        container.bleHelper.setupBeaconScanning()
        //Separate thread required to execute periodic requests
        // more regularly than ~15 minutes w/ Android killing it
        //runBlocking as we want this to be in order
        executor.execute {
            runBlocking {
                while (true) {
                    container.locationRepository.updateRecentLocation()
                    val beacons = container.logRepository.consumeLog()
                    if (beacons.entries.isNotEmpty()) {
                        val success =
                            try {
                                container.ownedTagsRepository.addLog(beacons)
                                container.networkLocatorRepository.submitLog(beacons)
                            } catch (e: IOException) {
                                Log.d(uk.ac.cam.smw98.bletracker.BackgroundApplication.Companion.TAG, e.toString())
                                listOf(-2)
                            } catch (e: HttpException) {
                                Log.d(uk.ac.cam.smw98.bletracker.BackgroundApplication.Companion.TAG, e.message())
                                listOf(-2)
                            }
                            catch(e: SocketTimeoutException){
                                Log.d(uk.ac.cam.smw98.bletracker.BackgroundApplication.Companion.TAG, e.toString())
                                listOf(-2)
                            }
                        if (success.contains(-1)) {
                            Log.d(uk.ac.cam.smw98.bletracker.BackgroundApplication.Companion.TAG, "Error in submitting log of ${success.size} beacons")
                        } else if (success.size == 1 && success[0] == -2) {
                            Log.d(uk.ac.cam.smw98.bletracker.BackgroundApplication.Companion.TAG, "Connection Error on ${beacons.entries.size}")
                        }
                    }
                    //Simulate requesting location, using same method as locate viewmodel
                    /*try{
                        container.ownedTagsRepository.getRecentEntries(container.networkLocatorRepository.getLocations())
                    } catch (e: IOException) {
                        Log.d(TAG, e.toString())
                        listOf(-2)
                    } catch (e: HttpException) {
                        Log.d(TAG, e.message())
                        listOf(-2)
                    } catch(e: SocketTimeoutException){
                        Log.d(TAG, e.toString())
                        listOf(-2)
                    }
                    Thread.sleep(container.loggingPeriod / 2)
                    try{
                        container.ownedTagsRepository.getRecentEntries(container.networkLocatorRepository.getLocations())
                    } catch (e: IOException) {
                        Log.d(TAG, e.toString())
                        listOf(-2)
                    } catch (e: HttpException) {
                        Log.d(TAG, e.message())
                        listOf(-2)
                     } catch(e: SocketTimeoutException){
                        Log.d(TAG, e.toString())
                        listOf(-2)
                    }*/
                    Thread.sleep(container.loggingPeriod )
                    Log.d(uk.ac.cam.smw98.bletracker.BackgroundApplication.Companion.TAG, "I will log this line every ${container.loggingPeriod}.")
                }
            }
        }
    }

    companion object {
        const val TAG = "BackgroundApplication"
    }

}