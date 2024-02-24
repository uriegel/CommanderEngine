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
                    json("/getfiles") {
                        getFilesRoute(it.substring(9))
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

