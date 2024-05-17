package de.uriegel.commanderengine

import android.content.Context
import de.uriegel.commanderengine.httpserver.HttpServer
import de.uriegel.commanderengine.httpserver.http
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class Server {

    fun start(context: Context) {
        server = http {
            port(8080)
            corsDomain("http://localhost:5173")
            routing {
                get {
                    request("/getfiles") {
                        (getFilesRoute(it.url.substring(9), it, context))
                    }
                    request("/getfile") {
                        getFileRoute(it.url.substring(8), it, false)
                    }
                    request("/downloadfile") {
                        getFileRoute(it.url.substring(13), it, true)
                    }
                }
                put {
                    request("/putfile") {
                        putFileRoute(it.url.substring(8), it)
                    }
                }
                delete {
                    request("/deletefile") {
                        deleteFileRoute(it.url.substring(11), it)
                    }
                }
            }
        }
            .build()
            .start()
    }

    fun stop() {
        server?.stop()
    }

    private var server: HttpServer? = null
}

// TODO Delete Directory

