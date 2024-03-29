package com.example.bletracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bletracker.data.model.Entry
import com.example.bletracker.data.model.Position
import com.example.bletracker.data.model.Tag
import com.example.bletracker.data.model.UpdateUiState
import com.example.bletracker.data.utils.ble.toListEntry
import com.example.bletracker.ui.viewmodel.RegisterViewModel
import kotlinx.datetime.LocalDateTime
import org.altbeacon.beacon.Beacon
import java.util.UUID


@Composable
fun RegisterScreen(
    registerTagViewModel: RegisterViewModel,
    localTags: State<Collection<Beacon>>,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState
) {
    //Get state of tags, refresh when detected tags change
    val smoothTags =  registerTagViewModel.smoothBeacons(localTags.value)
    when (val uiState = registerTagViewModel.registerUiState) {
        is UpdateUiState.Idle -> {}
        is UpdateUiState.Success -> LaunchedEffect(uiState){  snackBarHostState.showSnackbar("Tag ${uiState.status} Registered")
        }
        is UpdateUiState.Loading ->LaunchedEffect(uiState){  snackBarHostState.showSnackbar("  Registering....")
        }
        is UpdateUiState.Error ->   LaunchedEffect(uiState){  snackBarHostState.showSnackbar(" Error: ${uiState.msg}")
    }
    }

    BLELocalTags(entries =smoothTags.toListEntry(), registerTag ={
    registerTagViewModel.registerTag(it)
     },modifier=modifier)

}

@Composable
fun BLELocalTags(entries: List<Entry>, registerTag: (Tag) -> Unit, modifier: Modifier = Modifier) {
    if(entries.isEmpty())
    {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.padding(vertical=24.dp)
        ) {
            Text(text = "No Nearby Tags")
        }
    }
    else {
        LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
            items(items = entries) { entry ->
                BLETagDisplay(entry = entry, registerTag=registerTag,modifier = modifier)
            }
        }
    }
}

@Composable
private fun BLETagDisplay(
    entry: Entry,
    registerTag : (Tag) -> Unit,
    modifier: Modifier = Modifier) {
    val showDialog = remember { mutableStateOf(false) }
    val chosenTag = remember { mutableStateOf( Tag(0U,0U,UUID(0,0)))}
    if (showDialog.value) {
        RegisterTagDialog(
            tag = chosenTag.value,
            showDialog = showDialog.value,
            onConfirm = registerTag,
            onDismiss = { showDialog.value = false })
    }
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = "UUID: ${entry.tag.uuid} Major: ${entry.tag.major}, Minor: ${entry.tag.minor}")
                Text(text = "Approx.: %.4fm".format(entry.distance))
            }
            ElevatedButton(onClick = {
                chosenTag.value =entry.tag
                showDialog.value = true
            }) {
                Text("Register")
            }
        }
    }
}
    @Composable
    fun RegisterTagDialog(tag: Tag, showDialog: Boolean, onConfirm: (Tag)->Unit, onDismiss: () -> Unit) {
        if (showDialog) {
            AlertDialog(
                title = { Text("Register this Tag?") },
                text = { Text("UUID: ${tag.uuid} Major: ${tag.major}, Minor: ${tag.minor}") },
                onDismissRequest =  onDismiss ,
                confirmButton = {
                    Button(onClick = {
                        onConfirm(tag)
                        onDismiss()
                    }) { Text("REGISTER") }
                },
                dismissButton = { Button(onClick = onDismiss) { Text("CANCEL") } }
            )
        }
    }

@Preview(showBackground = true)
@Composable
fun BLEResultScreenPreview() {
        BLELocalTags(listOf(
            Entry(
                time = LocalDateTime(2024,12,14,9,55,0),
                tag =  Tag(0U,0U, UUID(0,0)),
                tagID = 1,
                distance =  3.0,
                position = Position(0.456,0.3456)
            ),
                    Entry(
                    time = LocalDateTime(2025,12,14,9,55,0),
            tag =  Tag(0U,0U, UUID(0,0)),
            tagID = 1,
            distance =  3.0,
            position = Position(0.456,0.3456)
        )
        ), registerTag = {}, modifier = Modifier.fillMaxWidth())
}

@Preview(showBackground = true)
@Composable
fun RegDialogPreview() {
    RegisterTagDialog(tag = Tag(45U,45U, UUID(0,1)),onConfirm={},showDialog = true) {

    }
}