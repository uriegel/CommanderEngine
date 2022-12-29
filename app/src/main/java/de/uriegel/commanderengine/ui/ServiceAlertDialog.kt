package de.uriegel.commanderengine.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.uriegel.commanderengine.R

@Composable
fun ServiceAlertDialog(onDismiss: () -> Unit, onOk: ()->Unit) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onOk()
            }) { Text(text = stringResource(id = R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        title = { Text(text = stringResource(id = R.string.alert_title_stop_service)) },
        text = { Text(text = stringResource(id = R.string.alert_stop_service)) }
    )
}