package de.uriegel.commanderengine

import de.uriegel.commanderengine.httpserver.HttpServer
import de.uriegel.commanderengine.httpserver.http
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class Server {

    fun start() {
        server = http {
            port(8080)
            corsDomain("http://localhost:5173")
            routing {
                get {
                    json("/json1") {
                        val data = Data("path/url", "Uwe Riegel", 9865)
                        Json.encodeToString(data)
                    }
                    json("/json2") {
                        val data = Data("path/url2", "Fredy Riegel", 1211)
                        Json.encodeToString(data)
                    }
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

@Serializable
data class Data(val path: String, val name: String, val nr: Int)

// TODO Delete Directory

