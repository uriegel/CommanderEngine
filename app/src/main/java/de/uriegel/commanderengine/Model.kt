package de.uriegel.commanderengine

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Model : ViewModel() {
    val serviceRunning: MutableLiveData<Boolean> = Service.running
}