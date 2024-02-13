package de.uriegel.commanderengine

import de.uriegel.commanderengine.httpserver.HttpServer

class Server {

    fun start() {
        server.start()
    }

    fun stop() {
 //       server.stop()
    }

    private val server = HttpServer(8080)
//        Javalin
//            .create() {
//                it.jetty.defaultPort = 8080
//            }
//            .get("/remote/getfiles{path}") {
//                val email = it.pathParam("path")
//                val affe = email
//            }

//    private val server = embeddedServer(Netty, 8080) {
//        install(ContentNegotiation) {
//            gson ()
//        }
//        install(CORS) {
//            anyHost()
//            allowHeader(HttpHeaders.AccessControlAllowHeaders)
//            allowHeader(HttpHeaders.ContentType)
//            allowHeader(HttpHeaders.AccessControlAllowOrigin)
//            allowMethod(HttpMethod.Options)
//        }
//        install(AutoHeadResponse)
//        install(PartialContent)
//
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

