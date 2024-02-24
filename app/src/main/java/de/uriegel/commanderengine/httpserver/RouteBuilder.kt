package de.uriegel.commanderengine.httpserver

class RouteBuilder() {

    fun request(context: HttpContext) =
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

    fun request(path: String, initializer: (context: HttpContext)->Unit) {
        requests[path] = ({ initializer(it)})
    }

    private val requests = mutableMapOf<String, (context: HttpContext)->Unit>()
}