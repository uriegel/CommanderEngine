package de.uriegel.commanderengine.httpserver

class RoutingBuilder {

    var get: RouteBuilder? = null
    var post: RouteBuilder? = null
    var put: RouteBuilder? = null

    fun get(initializer: RouteBuilder.() -> Unit) {
        get = RouteBuilder().apply(initializer)
    }

    fun post(initializer: RouteBuilder.() -> Unit) {
        post = RouteBuilder().apply(initializer)
    }

    fun put(initializer: RouteBuilder.() -> Unit) {
        put = RouteBuilder().apply(initializer)
    }
}