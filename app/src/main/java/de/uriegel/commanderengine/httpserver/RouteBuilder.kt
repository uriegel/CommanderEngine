package de.uriegel.commanderengine.httpserver

class RouteBuilder() {

    fun request(url: String, sendJson: (json: String)->String?) =
        jsons.keys.firstOrNull{ url.startsWith(it) }?.let {
            jsons[it]
                ?.invoke(url)
                ?.let(sendJson)
        }

    fun json(path: String, initializer: (url: String) -> String) {
        jsons[path] = ({ initializer(it)})
    }

    private val jsons = mutableMapOf<String, (url: String)->String>()
}