package de.uriegel.commanderengine

import de.uriegel.commanderengine.httpserver.HttpServer
import de.uriegel.commanderengine.httpserver.http
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class Server {

    fun start() {
        server = http {
            port(8080)
            corsDomain("http://localhost:5173")
            routing {
                get {
                    request("/getfiles") {
                        (getFilesRoute(it.url.substring(9), it))
                    }
                    request("/getfile") {
                        getFileRoute(it.url.substring(8), it)
                    }
                }
                post {
                    request("/postfile") {
                        postFileRoute(it.url.substring(9), it)
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

