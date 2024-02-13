package de.uriegel.commanderengine.httpserver

import android.util.Log
import java.net.ServerSocket
import kotlinx.coroutines.*

class HttpServer(port: Int) {

    private val server: ServerSocket

    fun start() {
        CoroutineScope(Dispatchers.Default).async {
            launch(newSingleThreadContext("MyOwnThread")) {
                while (true) {
                    Log.d("TAG", "start1: ")
                    val client = server.accept()
                    launch(Dispatchers.Default) {
                        Log.d("TAG", "start2: ")
                        val affe = client
                        val imist = 9
                        Log.d("TAG", "start: ")
                    }
                }
            }
        }
    }
    init {
        server = ServerSocket(port)
    }
}