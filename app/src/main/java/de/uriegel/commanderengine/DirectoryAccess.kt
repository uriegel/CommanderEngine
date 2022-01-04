package de.uriegel.commanderengine

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable

fun Route.testRoute() {
    route("/getfiles") {
        get {
            val items = listOf<Item>(Item("Bild3.jpg", 23452),
                Item("Bild345.jpg", 3245345),
                Item("Bild333.jpg", 74556789)
            )
            val result = ItemResult("/home/uwe/Pictures", items)
            call.respond(result)
        }
    }
}

fun Route.testPostRoute() {
    route("/postfiles") {
        post {
            val params = call.receive<Params>()
            val path = params.path
            val items = listOf<Item>(Item("Movie23.mp4", 23452),
                Item("Movie34345.mp4", 3245345),
                Item("Movie435333.mp4", 74556789),
                Item("Movie2222333.mp4", 7455678569)
            )
            val result = ItemResult("/home/uwe/Pictures", items)
            call.respond(result)
        }
    }
}

@Serializable
data class ItemResult(val path: String, val items: List<Item>)

@Serializable
data class Item(val name: String, val size: Long)

@Serializable
data class Params(val path: String)