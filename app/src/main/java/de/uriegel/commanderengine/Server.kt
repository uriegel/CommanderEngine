package de.uriegel.commanderengine

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import io.ktor.serialization.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

data class SseEvent(val data: String, val event: String? = null, val id: String? = null)

class Server {

    fun start() { server.start(wait = false)}

    fun stop() { server.stop(1, 5, TimeUnit.SECONDS)}

//    @OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
    private val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            gson ()
        }
        install(CORS) {
            anyHost()
        }

        val channel = produce { // this: ProducerScope<SseEvent> ->
            var n = 0
            while (true) {
                send(SseEvent("demo$n"))
                delay(1000)
                n++
            }
        }.broadcast()

        routing {
            route("/sse") {
                get {
                    val events = channel.openSubscription()
                    try {
                        call.respondSse(events)
                    } finally {
                        events.cancel()
                    }
                }
            }
            getFilesRoute()
            getFileRoute()
            getFilesInfosRoute()
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun ApplicationCall.respondSse(events: ReceiveChannel<SseEvent>) {
    response.cacheControl(CacheControl.NoCache(null))
    respondTextWriter(contentType = ContentType.Text.EventStream) {
        for (event in events) {
            if (event.id != null) {
                write("id: ${event.id}\n")
            }
            if (event.event != null) {
                write("event: ${event.event}\n")
            }
            for (dataLine in event.data.lines()) {
                write("data: $dataLine\n")
            }
            write("\n")
            flush()
        }
    }
}