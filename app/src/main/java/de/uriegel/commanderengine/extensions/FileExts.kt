package de.uriegel.commanderengine.extensions

import java.io.File

fun File.deleteRecursive() {
    if (this.isDirectory)
        for (child in this.listFiles() ?: arrayOf<File>())
            child.deleteRecursive()
    this.delete()
}