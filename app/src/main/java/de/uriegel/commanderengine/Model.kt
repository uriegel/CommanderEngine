package de.uriegel.commanderengine

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import de.uriegel.commanderengine.android.Service

class Model : ViewModel() {
    val servicePending: MutableState<Boolean> = Service.pending
    val serviceRunning: MutableState<Boolean> = Service.running
}