package com.example.bletracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.bletracker.R
import com.example.bletracker.ui.theme.MarsPhotosTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.bletracker.data.source.network.model.Entries
import com.example.bletracker.data.source.network.model.Entry
import com.example.bletracker.data.source.network.model.Tag


@Composable
fun RegisterScreen(
     registerUiState: RegisterUiState,
    retryAction : () -> Unit,
    modifier: Modifier = Modifier
) {
    when (registerUiState) {
        is RegisterUiState.BLESuccess -> BLEResultScreen(
            registerUiState.tags, modifier = modifier.fillMaxWidth()
        )
        is RegisterUiState.Error -> ErrorScreen(retryAction,registerUiState.msg, modifier = modifier.fillMaxSize())
    }
}
/**
 * ResultScreen displaying number of photos retrieved.
 */
@Composable
fun BLEResultScreen(tags: Entries, modifier: Modifier = Modifier) {
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
                BLETagDisplay(tag = tag, modifier = modifier)
            }
        }
    }
}

@Composable
fun RegisterTagDialog(tag: Tag, showDialog: Boolean, onDismiss: () -> Unit) {
    if(showDialog) {
        AlertDialog(
            title = { Text("Register this Tag?") },
            onDismissRequest = { onDismiss },
            confirmButton = {Button(onClick ={/*TODO FOR VIEW MODEL*/}){Text("REGISTER")}},
            dismissButton = { Button(onClick =onDismiss){Text("CANCEL")} }
        )
    }
}

@Composable
private fun BLETagDisplay(
    tag: Entry,
    modifier: Modifier = Modifier) {
    var showDialog = remember { mutableStateOf(false) }
    if (showDialog.value){
        RegisterTagDialog(
            tag = tag.tag,
            showDialog = showDialog.value,
            onDismiss = {showDialog.value = false})}
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
            ElevatedButton(onClick = {showDialog.value = true}){
                    Text("Register")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BLEResultScreenPreview() {
    MarsPhotosTheme {
        BLEResultScreen(Entries(listOf()))
    }
}