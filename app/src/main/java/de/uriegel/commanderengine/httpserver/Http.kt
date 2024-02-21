package de.uriegel.commanderengine.httpserver

fun http(initializer: Builder.() -> Unit): Builder {
    return Builder().apply(initializer)
}