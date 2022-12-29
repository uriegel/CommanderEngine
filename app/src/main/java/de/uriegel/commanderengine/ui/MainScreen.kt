package de.uriegel.commanderengine.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import de.uriegel.commanderengine.Model
import de.uriegel.commanderengine.R

@Composable
fun MainScreen(start: ()->Unit, stop: ()->Unit,
               padding: PaddingValues = PaddingValues(), viewModel: Model = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog)
        ServiceAlertDialog(
            { showDialog = false },
            {
                viewModel.servicePending.value = true
                stop()
            }
        )
    ConstraintLayout(
        modifier =
        Modifier
            .padding(padding)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val (buttonStart, buttonStop) = createRefs()

        Button(
            enabled = !viewModel.servicePending.value && !viewModel.serviceRunning.value,
            onClick = {
                viewModel.servicePending.value = true
                start()
            },
            modifier = Modifier.constrainAs(buttonStart) {
                top.linkTo(parent.top)
                bottom.linkTo(buttonStop.top)
                centerHorizontallyTo(parent)
            }
        ) {
            Text(stringResource(id = R.string.btn_start))
        }
        Button(
            enabled = !viewModel.servicePending.value && viewModel.serviceRunning.value,
            onClick = { showDialog = true },
            modifier = Modifier.constrainAs(buttonStop) {
                top.linkTo(buttonStart.bottom)
                bottom.linkTo(parent.bottom)
                centerHorizontallyTo(parent)
            }
        ) {
            Text(stringResource(id = R.string.btn_stop))
        }
    }
}