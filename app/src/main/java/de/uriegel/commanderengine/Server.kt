package de.uriegel.commanderengine

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.concurrent.TimeUnit

class Server {

    fun start() { server.start(wait = false)}

    fun stop() { server.stop(1, 5, TimeUnit.SECONDS)}

    private val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            gson {}
        }
        routing {
            get("/") {
                call.respond(mapOf("message" to "Hallo Wörlt"))
            }
        }
    }
}