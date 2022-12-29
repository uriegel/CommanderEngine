package de.uriegel.commanderengine

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uriegel.commanderengine.android.Service

class Model : ViewModel() {
    val serviceRunning: MutableLiveData<Boolean> = Service.running
}