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
 */
package com.example.bletracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bletracker.data.ble.BeaconRangingSmoother
import com.example.bletracker.data.repository.LocatorRepository
import com.example.bletracker.data.source.network.model.DeviceID
import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.Entry
import com.example.bletracker.data.source.network.model.LogStatus
import com.example.bletracker.data.source.network.model.Position
import com.example.bletracker.data.source.network.model.RegisterStatus
import com.example.bletracker.data.source.network.model.Registrator
import com.example.bletracker.data.source.network.model.Tag
import kotlinx.datetime.LocalDateTime
import org.altbeacon.beacon.AltBeacon
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.RegionViewModel
import java.util.UUID

@Composable
fun ScreenTabLayout(regionViewModel:RegionViewModel,
                        snackBarHostState : SnackbarHostState,
                        modifier: Modifier = Modifier,
                       locatorViewModel: LocateViewModel = viewModel(factory=LocateViewModel.Factory),
                      registerTagViewModel: RegisterTagViewModel = viewModel(factory=RegisterTagViewModel.Factory)
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

        when (tabIndex.intValue) {
            0 -> RegisterScreen(registerTagViewModel = registerTagViewModel,
                localTags = regionViewModel.rangedBeacons.observeAsState(initial= mutableListOf()),
                snackBarHostState = snackBarHostState)
            1 -> { locatorViewModel.getOwnedTags()
                LocateScreen(locatorViewModel = locatorViewModel)}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview(){
    val regionViewModel =  RegionViewModel()
    ScreenTabLayout(
        regionViewModel = RegionViewModel(),
        snackBarHostState = SnackbarHostState(),
        locatorViewModel = LocateViewModel(FakeNetworkLocatorRepository()),
        registerTagViewModel = RegisterTagViewModel(FakeNetworkLocatorRepository()),

        )
}

class FakeNetworkLocatorRepository(): LocatorRepository {
    override suspend fun getLocations(): Entries {
        return FakeDataSource.locatorEntries
    }

    override suspend fun submitLog(entries: Entries): List<Int> {
        return FakeDataSource.logStatusSuccess.status
    }

    override suspend fun registerTag(tag: Tag, mode: Boolean): Int {
        return FakeDataSource.registerStatusSuccess.status
    }
}


object FakeDataSource {

    val deviceID = DeviceID(2)

    val locatorEntries= Entries(listOf(
        Entry(
            time= LocalDateTime(2024,12,14,9,55,0) ,
            tag  =  Tag(0U,0U, UUID(0,0)),
            tagID = 1,
            distance =  3.0,
            position = Position(0.456,0.3456)
        ),
        Entry(
            time= LocalDateTime(2025,12,14,9,55,0) ,
            tag  =  Tag(0U,0U, UUID(0,0)),
            tagID = 3,
            distance =  4.0,
            position = Position(0.456,0.3456)
        )
    )
    )
    val logEntries= Entries(listOf(
        Entry(
            time= LocalDateTime(2021,1,22,12,30,12) ,
            tag  =  Tag(43U,1026U, UUID(654,2222)),
            tagID = 0,
            distance =  4.4,
            position = Position(52.19,0.56)
        ),
        Entry(
            time= LocalDateTime(2021,2, 21,9,20,11) ,
            tag  =  Tag(1234U,12U, UUID(653,2245)),
            tagID = 0,
            distance =  4.4,
            position = Position(52.19,0.56)
        ),
        Entry(
            time= LocalDateTime(2011,2, 21,9,20,11) ,
            tag  =  Tag(1234U,12U, UUID(653,2245)),
            tagID = 0,
            distance =  4.4,
            position = Position(52.19,0.56)
        )
    )
    )
    val  registerStatusSuccess = RegisterStatus(35)
    val  registerStatusFail1 = RegisterStatus(-1)
    val  registerStatusFail12= RegisterStatus(-2)

    val  logStatusSuccess = LogStatus(listOf(0,0,0))
    val  logStatusFail1 = LogStatus(listOf(0,-1,-1))
    val  logStatusFail12= LogStatus(listOf(-1,-1,-1))

    val  logStatusDuplicate1 = LogStatus(listOf(-2,-2,-2))
    val  logStatusDuplicate2=  LogStatus(listOf(0,-1,-2))

    val registrator = Registrator(
        tag  =  Tag(43U,1026U, UUID(654,2222)),
        deviceId = 5,
        mode = true
    )



}
