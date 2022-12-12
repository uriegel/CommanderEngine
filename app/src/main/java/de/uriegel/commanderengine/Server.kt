package de.uriegel.commanderengine

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.concurrent.TimeUnit
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.routing.*

class Server {

    fun start() { server.start(wait = false)}

    fun stop() { server.stop(1, 5, TimeUnit.SECONDS)}

    private val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            gson ()
        }
        install(CORS) {
            anyHost()
        }
        install(AutoHeadResponse)
        install(PartialContent)

        routing {
            getFilesRoute()
            getFileRoute()
            getFilesInfosRoute()
            postFileRoute()
            getImageRoute()
            getVideoRoute()
        }
    }
}
