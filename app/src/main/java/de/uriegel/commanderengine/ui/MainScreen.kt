package de.uriegel.commanderengine.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import de.uriegel.commanderengine.Model
import de.uriegel.commanderengine.R
import java.net.Inet4Address
import java.net.NetworkInterface

@Composable
fun MainScreen(start: ()->Unit, stop: ()->Unit,
               padding: PaddingValues = PaddingValues(), viewModel: Model = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    val ip by remember { mutableStateOf(
        NetworkInterface
            .getNetworkInterfaces()
            .asSequence()
            .flatMap {
                it
                    .inetAddresses
                    .asSequence()
                    .filter { inet -> !inet.isLoopbackAddress && inet is Inet4Address }
                    .map { inet4 -> inet4.hostAddress }
            }.first{ it.startsWith("192.") }) }
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
        val (buttonStart, buttonStop, ipText) = createRefs()

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
                bottom.linkTo(ipText.top)
                centerHorizontallyTo(parent)
            }
        ) {
            Text(stringResource(id = R.string.btn_stop))
        }
        Text(text = ip,
            modifier = Modifier.constrainAs(ipText) {
                top.linkTo(buttonStop.top)
                bottom.linkTo(parent.bottom)
                centerHorizontallyTo(parent)
            }
        )
    }
}