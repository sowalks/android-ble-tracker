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

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bletracker.R
import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.Entry
import com.example.bletracker.data.source.network.model.Position
import com.example.bletracker.data.source.network.model.Tag
import com.example.bletracker.data.source.network.model.UpdateUiState
import com.example.bletracker.ui.screens.LocateViewModel.Companion.TAG
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.util.UUID


@Composable
fun LocateScreen(
    locatorViewModel: LocateViewModel,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState
) {
   when(val mode = locatorViewModel.setModeState) {
        is UpdateUiState.Idle -> {
            Log.d(TAG,"no change - idle")}
        is UpdateUiState.Success -> LaunchedEffect(mode) {
            snackBarHostState.showSnackbar(
                when (mode.status) {
                    0 -> "Mode set to Inhibitor."
                    1 -> "Mode set to Tracking."
                    else -> "Error, Try Again."
                }
            )
        }
        is UpdateUiState.Loading -> LaunchedEffect(mode) {
            snackBarHostState.showSnackbar("Setting Mode... ")
        }
        is UpdateUiState.Error -> LaunchedEffect(mode) {
            snackBarHostState.showSnackbar("Error, Try Again,")
        }
    }

    when (val uiState= locatorViewModel.locatorUiState) {
        is LocatorUiState.Success-> ResultScreen(
            locatorViewModel.tags, setMode = {tagID,mode-> locatorViewModel.setTagMode(tagID,mode) }, modifier = modifier.fillMaxWidth()
        )
        is LocatorUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is LocatorUiState.Error -> {
            ErrorScreen({ locatorViewModel.getOwnedTags() },"Try again: ${uiState.msg}", modifier = modifier.fillMaxSize())
        }
    }
}

@Composable
fun ResultScreen(tags: Entries, setMode: (Int,Boolean)->Unit,modifier: Modifier = Modifier) {
    if(tags.entries.isEmpty())
    {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {
            Text(text = "Owned Tags could not be Located")
        }
    }
    else {
        LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
            items(items = tags.entries) { tag ->
                OwnedTagDisplay(tag = tag, setMode = setMode, modifier = modifier)
            }
            }
        }
    }


@Composable
private fun OwnedTagDisplay(tag: Entry,
                            setMode: (Int,Boolean)->Unit,
                       modifier: Modifier = Modifier){
    val showDialog = remember { mutableStateOf(false) }
    //set mode when button clicked
    if (showDialog.value){
        SetModeDialog(
            tagID = tag.tagID,
            showDialog = showDialog.value,
            onDismiss = {showDialog.value = false},
            onConfirm =setMode) }
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = tag.tagID.toString())
                Text(text = "Last seen %.4f from (${tag.position.latitude},${tag.position.longitude}) at ${tag.time}".format(tag.distance))
            }
            ElevatedButton(onClick = {showDialog.value = true}){
                Text("Set Mode")
            }
        }
    }
}

/* Display loading symbol While Waiting*/
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, msg : String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = msg, modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text("Retry")
        }
    }
}



@Composable
fun SetModeDialog(tagID: Int, showDialog: Boolean, onConfirm: (Int,Boolean)->Unit,onDismiss : () -> Unit){
    if(showDialog) {
        var modeState by remember { mutableStateOf(true) }
        AlertDialog(
            title = { Text("Set Tag $tagID's Mode") },
            text = {
                Column(Modifier.selectableGroup())
                {
                    Row {
                        RadioButton(selected = modeState, onClick = { modeState = true })
                        Text("Tracker Mode")
                    }
                    Row {
                        RadioButton(selected = !modeState, onClick = { modeState = false })
                        Text("Inhibitor Mode")
                    }
                }
            },
            onDismissRequest = { onDismiss() },
            confirmButton = {Button(onClick ={
                    onConfirm(tagID,modeState)
                    onDismiss()
            }){Text("SET MODE")}},
            dismissButton = {Button(onClick = onDismiss){Text("CANCEL")} }
            )
    }
}



@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    ResultScreen(Entries(listOf(
        Entry(
            time = LocalDateTime(2024,12,14,9,55,0).toInstant(timeZone = TimeZone.currentSystemDefault()) ,
            tag =  Tag(0U,0U, UUID(0,0)),
            tagID = 1,
            distance =  3.0,
            position = Position(0.456,0.3456)
        )
    )),setMode ={_,_->},modifier = Modifier.fillMaxWidth())}

@Preview(showBackground = true)
@Composable
fun DialogPreview() {
    SetModeDialog(tagID = 6, onConfirm = {_,_ ->}, showDialog = true) {
    }
}