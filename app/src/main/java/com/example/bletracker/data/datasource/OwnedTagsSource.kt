package com.example.bletracker.data.datasource

import android.util.Log
import androidx.datastore.core.DataStore
import com.example.bletracker.data.ProtoOwnedEntries
import com.example.bletracker.data.ProtoOwnedEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException


class OwnedTagsSource(
    private val ownedTagsDataStore: DataStore<ProtoOwnedEntries>
) {
    private val TAG: String = "LocalOwnedTagsRepo"

    val ownedTagsFlow: Flow<ProtoOwnedEntries> = ownedTagsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading.", exception)
                emit(ProtoOwnedEntries.getDefaultInstance())
            } else {
                throw exception
            }
        }


    suspend fun setAllTags(ownedEntries: List<ProtoOwnedEntry>) {
        Log.e(TAG, "Stored new list ${ownedEntries.toString()}")
        ownedTagsDataStore.updateData { tagStore ->
            tagStore.toBuilder()
                .clearOwnedtag()
                .addAllOwnedtag(ownedEntries)
                .build()
        }
    }

    suspend fun addNewTag(ownedEntry: ProtoOwnedEntry) {
        Log.e(TAG, "Stored new entry ${ownedEntry.toString()}")
        ownedTagsDataStore.updateData { tagStore ->
            tagStore.toBuilder()
                .addOwnedtag(ownedEntry)
                .build()
        }
    }


}