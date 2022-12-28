package de.uriegel.commanderengine.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ServiceAlertDialog(onDismiss: () -> Unit, onOk: ()->Unit) {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onOk()
            }) { Text(text = "OK") }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = "Please confirm") },
        text = { Text(text = "Should I continue with the requested action?") }
    )
}