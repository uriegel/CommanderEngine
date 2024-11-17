package de.uriegel.commanderengine.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.uriegel.commanderengine.R
import de.uriegel.commanderengine.ui.theme.CommanderEngineTheme

@Preview(showBackground = true, showSystemUi = true, apiLevel = 35)
@Composable
fun DefaultPreview() {
    CommanderEngineTheme {
        MainScreen({}, {})
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 35)
@Composable
fun ServiceAlertPreview() {
    ServiceAlertDialog({}, {})
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 35)
@Composable
fun TestDialogScreen(padding: PaddingValues = PaddingValues()) {
    DialogScreen(R.string.PERMISSION_DENIED, padding)
}
