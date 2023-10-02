package de.uriegel.commanderengine.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun DialogScreen(stringId: Int, padding: PaddingValues = PaddingValues()) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(padding)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Text(
            stringResource(stringId),
            modifier = Modifier.padding(horizontal = 30.dp))
    }
}
