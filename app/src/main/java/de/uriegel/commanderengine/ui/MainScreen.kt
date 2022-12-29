package de.uriegel.commanderengine.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import de.uriegel.commanderengine.Model

@Composable
fun MainScreen(start: ()->Unit, stop: ()->Unit, viewModel: Model = viewModel()) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text("Commander Engine")
        })
    }, content = {
        var showDialog by remember { mutableStateOf(false) }
        if (showDialog)
            ServiceAlertDialog(
                { showDialog = false },
                {
                    viewModel.servicePending.value = true
                    stop()
                }
            )
        ConstraintLayout(modifier =
            Modifier
                .padding(it)
                .fillMaxWidth()
                .fillMaxHeight()
            ){
                val (buttonStart, buttonStop) = createRefs()

                Button(
                    enabled = !viewModel.servicePending.value && !viewModel.serviceRunning.value,
                    onClick = {
                        viewModel.servicePending.value = true
                        start() },
                    modifier = Modifier.constrainAs(buttonStart) {
                        top.linkTo(parent.top)
                        bottom.linkTo(buttonStop.top)
                        centerHorizontallyTo(parent)
                    }
                ) {
                    Text("Start")
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
                    Text("Stop")
                }
            }
        }
    )
}
