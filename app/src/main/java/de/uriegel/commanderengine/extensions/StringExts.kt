package de.uriegel.commanderengine.extensions

fun String.cutAt(char: Char): String {
    val pos = this.indexOf(char)
    return if (pos != -1)
            this.substring(0, pos)
        else
            this
}