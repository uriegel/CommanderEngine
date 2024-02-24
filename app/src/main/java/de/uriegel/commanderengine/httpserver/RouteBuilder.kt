package de.uriegel.commanderengine.httpserver

class RouteBuilder() {

    suspend fun request(context: HttpContext) =
        if (requests
            .keys
            .firstOrNull{ context.url.startsWith(it) }
            ?.let {
                requests[it]
                    ?.invoke(context)
                    ?.let{
                        true
                    }
            } == true)
            null
        else
            this

    fun request(path: String, initializer: suspend (context: HttpContext)->Unit) {
        requests[path] = ({ initializer(it)})
    }

    private val requests = mutableMapOf<String, suspend (context: HttpContext)->Unit>()
}