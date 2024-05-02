package uk.ac.cam.smw98.bletracker.ui.screens

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
import uk.ac.cam.smw98.bletracker.data.model.Entry
import uk.ac.cam.smw98.bletracker.data.model.Position
import uk.ac.cam.smw98.bletracker.data.model.Tag
import uk.ac.cam.smw98.bletracker.data.model.UpdateUiState
import uk.ac.cam.smw98.bletracker.data.utils.ble.toListEntry
import uk.ac.cam.smw98.bletracker.ui.viewmodel.RegisterViewModel
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
fun BLELocalTags(entries: List<Entry>, registerTag: (Entry) -> Unit, modifier: Modifier = Modifier) {
    //Display all nearby entries
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
            //sort by nearest, makes registering easier
            items(items = entries.sortedBy{it.distance}) { entry ->
                BLETagDisplay(entry = entry, registerTag=registerTag,modifier = modifier)
            }
        }
    }
}

@Composable
private fun BLETagDisplay(
    entry: Entry,
    registerTag : (Entry) -> Unit,
    modifier: Modifier = Modifier) {
    //Chosen entry required if entry list changes while register dialog is still open
    val showDialog = remember { mutableStateOf(false) }
    val chosenEntry = remember { mutableStateOf(Entry(position=Position(-200.0,-200.0)))}
    if (showDialog.value) {
        RegisterTagDialog(
            entry = chosenEntry.value,
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
                chosenEntry.value =entry
                showDialog.value = true
            }) {
                Text("Register")
            }
        }
    }
}
    @Composable
    fun RegisterTagDialog(entry: Entry, showDialog: Boolean, onConfirm: (Entry)->Unit, onDismiss: () -> Unit) {
        if (showDialog) {
            AlertDialog(
                title = { Text("Register this Tag?") },
                text = { Text("UUID: ${entry.tag.uuid} Major: ${entry.tag.major}, Minor: ${entry.tag.minor}") },
                onDismissRequest =  onDismiss ,
                confirmButton = {
                    Button(onClick = {
                        onConfirm(entry)
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
                tag = Tag(0U,0U),
                tagID = 1,
                distance =  3.0,
                position = Position(0.456,0.3456)
            ),
                    Entry(
                    time = LocalDateTime(2025,12,14,9,55,0),
            tag =  Tag(0U,0U),
            tagID = 1,
            distance =  3.0,
            position = Position(0.456,0.3456)
        )
        ), registerTag = {}, modifier = Modifier.fillMaxWidth())
}

@Preview(showBackground = true)
@Composable
fun RegDialogPreview() {
    RegisterTagDialog(entry=   Entry(tag=Tag(45U,45U, UUID(0,1)),position=Position(0.0,0.0)),onConfirm={},showDialog = true) {

    }
}