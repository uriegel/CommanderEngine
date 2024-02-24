package de.uriegel.commanderengine.httpserver

class RouteBuilder() {

    fun request(url: String) =
        jsons.keys.firstOrNull{ url.startsWith(it) }?.let {
            jsons[it]
                ?.invoke(url)
                ?.let{
                    RequestResult(it.toByteArray(), "application/json")
                }
        }

    fun json(path: String, initializer: (url: String) -> String) {
        jsons[path] = ({ initializer(it)})
    }

    private val jsons = mutableMapOf<String, (url: String)->String>()
}