package de.uriegel.commanderengine.httpserver

class Builder {

    var port = 0
    var corsDomain: String? = null

    fun port(port: Int) {
        this.port = port
    }

    fun corsDomain(corsDomain: String) {
        this.corsDomain = corsDomain
    }

    fun build(): HttpServer {
        return HttpServer(this)
    }
}
