package com.example.bletracker.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.bletracker.data.source.network.model.Entry
import com.example.bletracker.data.source.network.model.Position
import com.example.bletracker.data.source.network.model.Tag
import kotlinx.datetime.LocalDateTime
import java.util.UUID
import com.example.bletracker.data.ble.toListEntry
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon


@Composable
fun RegisterScreen(
    registerTagViewModel: RegisterTagViewModel,
    localTags: State<Collection<Beacon>>,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState
) {
    //Get state of tags, refresh when detected tags change
BLELocalTags(tags =localTags.value.toListEntry(), registerTag ={
    registerTagViewModel.registerTag(it)
    val uiState =  registerTagViewModel.registerUiState
    when (uiState ) {
        is RegisterUiState.Idle -> {}
        is RegisterUiState.Success ->  {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    "Tag ${uiState.tagID} Registered"
                )
            }
        }
        is RegisterUiState.Loading -> {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    "Registering...."
                )
            }
        }
        is RegisterUiState.Error ->   {
            coroutineScope.launch() {
                snackBarHostState.showSnackbar(
                    uiState.msg)
            }
        }
    } },modifier=modifier)

}

/**
 * ResultScreen displaying number of photos retrieved.
 */
@Composable
fun BLELocalTags(tags: List<Entry>, registerTag: (Tag) -> Unit, modifier: Modifier = Modifier) {
    if(tags.isEmpty())
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
            items(items = tags) { tag ->
                BLETagDisplay(tag = tag, registerTag=registerTag,modifier = modifier)
            }
        }
    }
}

@Composable
private fun BLETagDisplay(
    tag: Entry,
    registerTag : (Tag) -> Unit,
    modifier: Modifier = Modifier) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        RegisterTagDialog(
            tag = tag.tag,
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
                Text(text = tag.tagID.toString())
                Text(text = "Last seen ${tag.distance} from ${tag.position} at ${tag.time}")
            }
            ElevatedButton(onClick = { showDialog.value = true }) {
                Text("Register")
            }
        }
    }
}
    @Composable
    fun RegisterTagDialog(tag: Tag, showDialog: Boolean, onConfirm: (Tag)->Unit, onDismiss: () -> Unit,) {
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
                time= LocalDateTime(2024,12,14,9,55,0) ,
                tag  =  Tag(0U,0U, UUID(0,0)),
                tagID = 1,
                distance =  3.0,
                position = Position(0.456,0.3456)
            ),
                    Entry(
                    time= LocalDateTime(2025,12,14,9,55,0) ,
            tag  =  Tag(0U,0U, UUID(0,0)),
            tagID = 1,
            distance =  3.0,
            position = Position(0.456,0.3456)
        )
        ), registerTag = {}, modifier = Modifier.fillMaxWidth())
}

@Preview(showBackground = true)
@Composable
fun RegDialogPreview() {
    RegisterTagDialog(tag = Tag(45U,45U, UUID(0,1)),onConfirm={},showDialog = true,) {

    }
}