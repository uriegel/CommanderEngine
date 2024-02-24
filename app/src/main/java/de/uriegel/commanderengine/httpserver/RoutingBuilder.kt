package de.uriegel.commanderengine.httpserver

class RoutingBuilder {

    var get: RouteBuilder? = null

    fun get(initializer: RouteBuilder.() -> Unit) {
        get = RouteBuilder().apply(initializer)
    }
}