package de.uriegel.commanderengine

import android.os.Environment
import de.uriegel.commanderengine.extensions.cutAt
import de.uriegel.commanderengine.extensions.deleteRecursive
import de.uriegel.commanderengine.httpserver.HttpContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun getFilesRoute(urlPath: String, context: HttpContext) {
    val path = "${Environment.getExternalStorageDirectory()}${urlPath}"
    val directory = File(path)
    context.sendJson(
        Json.encodeToString(
            ResultItem.ok(
                Result.success(
                    directory
                        .listFiles()
                        ?.filterNotNull()
                        ?.map {
                            FileItem(
                                it.name,
                                it.isDirectory,
                                it.length(),
                                it.name.startsWith('.'),
                                it.lastModified()
                            )
                        }
                        ?: listOf()))))
}

fun getFileRoute(urlPath: String, context: HttpContext, download: Boolean) {
    val file = File("${Environment.getExternalStorageDirectory()}${urlPath}".cutAt('?'))
    if (file.exists()) {
        val headers = if (download) {
            mutableMapOf(
                "Content-Disposition" to "attachment; filename=\"${file.name}\"",
                "x-file-date" to "${file.lastModified()}"
            )
        } else {
            mutableMapOf(
                "x-file-date" to "${file.lastModified()}"
            )
        }

        context.sendStream(file.inputStream(), file.length(), file.name, headers)
    }
    else
        context.sendNotFound()
}

fun putFileRoute(urlPath: String, context: HttpContext) {
    try {
        val file = File("${Environment.getExternalStorageDirectory()}${urlPath}".cutAt('?'))

        context.postStream(file.outputStream())
        context
            .headers["x-file-date"]
            ?.toLong()
            ?.also {
                file.setLastModified(it)
            }
        //file.renameTo(File("${Environment.getExternalStorageDirectory()}${call.request.queryParameters["path"]!!}"))
        context.sendNoContent()

    } catch (_: java.lang.Exception) {
        context.sendNotFound()
    }
}

fun deleteFileRoute(urlPath: String, context: HttpContext) {
    try {
        File("${Environment.getExternalStorageDirectory()}${urlPath}")
            .deleteRecursive()
        context.sendNoContent()
    } catch (_: java.lang.Exception) {
        context.sendNotFound()
    }
}

@Serializable
data class FileItem(val name: String, val isDirectory: Boolean, val size: Long, val isHidden: Boolean, val time: Long)

@Serializable
data class ResultItem<T>(val ok: T?) {

    companion object {
        fun <T> ok(value: Result<T>): ResultItem<T> =
            ResultItem(value.getOrNull())
    }
}

