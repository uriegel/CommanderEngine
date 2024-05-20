package de.uriegel.commanderengine.extensions

import android.webkit.MimeTypeMap
import java.net.URLDecoder

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

fun String.urlDecode(): String =
    URLDecoder.decode(this.cutAt('?').replace("+", "%2b"), "UTF-8")