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
            }
        }
            .build()
            .start()
    }

    fun stop() {
        server?.stop()
    }

    private var server: HttpServer? = null
//            .get("/remote/getfiles{path}") {
//                val email = it.pathParam("path")
//            }

//        routing {
//            getFilesRoute()
//            getFileRoute()
//            getFilesInfosRoute()
//            postFileRoute()
//            deleteFileRoute()
//            get()
//        }
//    }
}

// TODO Delete Directory

