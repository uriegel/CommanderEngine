package de.uriegel.commanderengine.httpserver

class Builder {

    var port = 0
    var corsDomain: String? = null
    var routing: RoutingBuilder? = null

    fun port(port: Int) {
        this.port = port
    }

    fun corsDomain(corsDomain: String) {
        this.corsDomain = corsDomain
    }

    fun routing(initializer: RoutingBuilder.() -> Unit) {
        routing = RoutingBuilder().apply(initializer)
        // TODO map of string (route)->RouteBuilder
        // TODO RouteBuilder: get post -> context => context.sendJson
    }

    fun build(): HttpServer {
        return HttpServer(this)
    }
}
