package de.uriegel.commanderengine.extensions

import android.webkit.MimeTypeMap
fun String.cutAt(char: Char): String {
    val pos = this.indexOf(char)
    return if (pos != -1)
            this.substring(0, pos)
        else
            this
}

fun String.contentType(): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(this)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}