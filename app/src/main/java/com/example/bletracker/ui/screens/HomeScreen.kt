/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified from MarsPhotos Code Lab.
 */
package com.example.bletracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bletracker.data.model.DeviceID
import com.example.bletracker.data.model.Entries
import com.example.bletracker.data.model.Entry
import com.example.bletracker.data.model.LogStatus
import com.example.bletracker.data.model.Position
import com.example.bletracker.data.model.Status
import com.example.bletracker.data.model.Tag
import com.example.bletracker.data.repository.LocationRepository
import com.example.bletracker.data.repository.NetworkRepository
import com.example.bletracker.data.repository.OwnedTagsRepository
import com.example.bletracker.ui.viewmodel.LocateViewModel
import com.example.bletracker.ui.viewmodel.RegisterViewModel
import kotlinx.datetime.LocalDateTime
import org.altbeacon.beacon.RegionViewModel
import java.util.UUID

@Composable
fun ScreenTabLayout(regionViewModel:RegionViewModel,
                    snackBarHostState : SnackbarHostState,
                    modifier: Modifier = Modifier,
                    locatorViewModel: LocateViewModel = viewModel(factory= LocateViewModel.Factory),
                    registerTagViewModel: RegisterViewModel = viewModel(factory= RegisterViewModel.Factory)
) {

    val tabs = listOf("Register Tags", "Locate Tags")
    val tabIndex = remember {
        mutableIntStateOf(value = 0)
    }
    Column(modifier = modifier
        ) {
        TabRow(selectedTabIndex = tabIndex.intValue, modifier = Modifier
            .fillMaxWidth()) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex.intValue == index,
                    onClick = { tabIndex.intValue = index},
                    content = {
                        when (index) {
                            0 -> Text(title, style = MaterialTheme.typography.headlineSmall)
                            1 -> Text(title, style = MaterialTheme.typography.headlineSmall)
                        }},
                    modifier = modifier.padding(vertical = 24.dp)
                )
            }
        }
        //Reset snackBar notifications on switch
        when (tabIndex.intValue) {
            0 -> {
                locatorViewModel.userNotified()
                RegisterScreen(registerTagViewModel = registerTagViewModel,
                localTags = regionViewModel.rangedBeacons.observeAsState(initial= mutableListOf()),
                snackBarHostState = snackBarHostState)}
            1 -> {
                registerTagViewModel.userNotified()
                locatorViewModel.getOwnedTags()
                LocateScreen(locatorViewModel = locatorViewModel,snackBarHostState=snackBarHostState)}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview(){
    ScreenTabLayout(
        regionViewModel = RegionViewModel(),
        snackBarHostState = SnackbarHostState(),
        locatorViewModel = LocateViewModel(FakeNetworkRepository(),FakeOwnedTagRepository()),
        registerTagViewModel = RegisterViewModel(FakeNetworkRepository(),FakeOwnedTagRepository(), FakeLocationRepository()),

        )
}

class FakeNetworkRepository: NetworkRepository {
    override suspend fun getLocations(): Entries {
        return FakeDataSource.locatorEntries
    }

    override suspend fun submitLog(entries: Entries): List<Int> {
        return FakeDataSource.logStatusSuccess.status
    }

    override suspend fun registerTag(tag: Tag, mode: Boolean): Int {
        return FakeDataSource.statusSuccess.status
    }
    override suspend fun setMode(tagID: Int, mode: Boolean): Int {
        return if(mode){
            1
        } else {
            0
        }
    }
}

class FakeLocationRepository:LocationRepository{
    override suspend fun addPosition(entry: Entry): Entry {
        return entry
    }

    override suspend fun updateRecentLocation() {
        return
    }

}

class FakeOwnedTagRepository: OwnedTagsRepository {

    override suspend fun addTag(entry: Entry, tagID: Int) {
        return
    }

    override suspend fun addLog(log: Entries) {
       return
    }

    override suspend fun getRecentEntries(log: Entries): Entries {
        return log
    }
}


object FakeDataSource {

    val deviceID = DeviceID(UUID(2L,2L))

    val locatorEntries= Entries(listOf(
        Entry(
            time = LocalDateTime(2024,12,14,9,55,0),
            tag =  Tag(0U,0U),
            tagID = 1,
            distance =  3.0,
            position = Position(0.456,0.3456)
        ),
        Entry(
            time = LocalDateTime(2025,12,14,9,55,0)  ,
            tag =  Tag(0U,0U),
            tagID = 3,
            distance =  4.0,
            position = Position(0.456,0.3456)
        )
    )
    )

    val  statusSuccess = Status(35)

    val  logStatusSuccess = LogStatus(listOf(0,0,0))

}
