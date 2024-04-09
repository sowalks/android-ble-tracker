package com.example.bletracker.data.repository

import com.example.bletracker.data.ProtoOwnedEntry
import com.example.bletracker.data.datasource.OwnedTagsSource
import com.example.bletracker.data.model.Entries
import com.example.bletracker.data.model.Entry
import com.example.bletracker.data.model.Position
import com.example.bletracker.data.model.Tag
import kotlinx.coroutines.flow.first
import kotlinx.datetime.toLocalDateTime
import java.util.UUID


interface OwnedTagsRepository {
    suspend fun addTag(entry: Entry,tagID: Int)
    suspend fun addLog(log: Entries)
    suspend fun getRecentEntries(log: Entries): Entries
}

class LocalOwnedTagsRepository(private val dataSourceTags: OwnedTagsSource): OwnedTagsRepository {
    // Add new tag to store of owned tags
    override suspend fun addTag(entry: Entry,tagID: Int) {
        dataSourceTags.addNewTag(
            ProtoOwnedEntry.newBuilder()
                .setTagId(tagID)
                .setDistance(entry.distance)
                .setMajor(entry.tag.major.toInt())
                .setMinor(entry.tag.minor.toInt())
                .setUuid(entry.tag.uuid.toString())
                .setLongitude(entry.position.longitude)
                .setLatitude(entry.position.latitude)
                .setTime(entry.time.toString())
                .build()
        )
    }

    //We want to add values we can update, as in getRecentEntries
    //But we don't need to return the new list
    override suspend fun addLog(log: Entries){
        val finalStore = mutableListOf<ProtoOwnedEntry>()
        dataSourceTags.ownedTagsFlow.first()
            .ownedtagList.forEach { ownedEntry ->
                // add owned entry,  or most recent entry in log
                // for each ownedEntry in store
                // either locally match uuid/major/minor, or tagid from network call
                var finalEntry = ownedEntry
                log.entries.reversed().forEach { newEntry ->
                    if ((newEntry.tag.uuid.toString() == finalEntry.uuid
                        && newEntry.tag.minor.toInt() == finalEntry.minor
                        && newEntry.tag.major.toInt() == finalEntry.major)
                        && newEntry.time > finalEntry.time.toLocalDateTime()
                    ) {
                        finalEntry =
                            ProtoOwnedEntry.newBuilder()
                                .setTagId(finalEntry.tagId)
                                .setDistance(newEntry.distance)
                                .setMajor(newEntry.tag.major.toInt())
                                .setMinor(newEntry.tag.minor.toInt())
                                .setUuid(newEntry.tag.uuid.toString())
                                .setLongitude(newEntry.position.longitude)
                                .setLatitude(newEntry.position.latitude)
                                .setTime(newEntry.time.toString())
                                .build()

                }
                    else if( newEntry.tagID == ownedEntry.tagId
                                && newEntry.time > finalEntry.time.toLocalDateTime())
                    {
                        finalEntry = ProtoOwnedEntry.newBuilder()
                            .setTagId(newEntry.tagID)
                            .setDistance(newEntry.distance)
                            .setMajor(ownedEntry.major)
                            .setMinor(ownedEntry.minor)
                            .setUuid(ownedEntry.uuid)
                            .setLongitude(newEntry.position.longitude)
                            .setLatitude(newEntry.position.latitude)
                            .setTime(newEntry.time.toString())
                            .build()
                    }
                }
                finalStore.add(finalEntry)
            }
            if(finalStore.isNotEmpty()) {
                dataSourceTags.setAllTags(finalStore)
            }
    }

    override suspend fun getRecentEntries(log: Entries): Entries {
        addLog(log)
        //If there are no tags in store, this will return empty list of
        //entries, otherwise will return the full entries stored
        return Entries(
            dataSourceTags.ownedTagsFlow.first()
                .ownedtagList.map{
                    Entry(
                        time = it.time.toLocalDateTime(),
                        tag = Tag(
                            it.major.toUShort(),
                            it.minor.toUShort(),
                            UUID.fromString(it.uuid)
                        ),
                        tagID = it.tagId,
                        distance =  it.distance,
                        position = Position(it.longitude,it.latitude)
                    )
                }
        )
    }
}