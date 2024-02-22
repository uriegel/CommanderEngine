package de.uriegel.commanderengine.httpserver

class RouteBuilder() {

    fun request(url: String, sendJson: (json: String)->String?) =
        jsons.keys.firstOrNull{ url.startsWith(it) }?.let {
            jsons[it]
                ?.invoke()
                ?.let(sendJson)
        }

    fun <T> json(path: String, initializer: () -> T) {
        jsons[path] = ({ initializer().toString()})
    }

    private val jsons = mutableMapOf<String, ()->String>()
}