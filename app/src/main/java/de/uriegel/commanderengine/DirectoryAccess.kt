package de.uriegel.commanderengine

import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import androidx.core.content.ContextCompat
import de.uriegel.commanderengine.extensions.deleteRecursive
import de.uriegel.commanderengine.extensions.urlDecode
import de.uriegel.commanderengine.httpserver.HttpContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun getFilesRoute(urlPath: String, httpContext: HttpContext, context: Context) {

    if (urlPath == "" || urlPath == "/") {
        val root = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            (context
                .getSystemService(Context.STORAGE_SERVICE) as StorageManager)
                .storageVolumes
                .map {
                    it.directory?.path
                }
        else
            // For devices below Android N, you can use ContextCompat.getExternalFilesDirs
            ContextCompat.getExternalFilesDirs(context, null)
                .map {
                    it.absolutePath
                }

        httpContext.sendJson(
            Json.encodeToString(
                root
                    .map {
                        FileItem(
                            it ?: "",
                            true,
                            0,
                            it?.startsWith('.') ?: false,
                            0
                        )
                    }))
    } else
        httpContext.sendJson(
            Json.encodeToString(
            File(urlPath.urlDecode())
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
                ?: listOf()))
}

fun getFileRoute(urlPath: String, context: HttpContext, download: Boolean) {
    val file = File(urlPath.urlDecode())
    if (file.exists() && file.isFile) {
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

fun getMetaData(urlPath: String, context: HttpContext) {
    val file = File(urlPath.urlDecode())

    val (len, lastModified) = if (file.exists()) {
        Pair(file.length(), file.lastModified())
    } else {
        Pair<Long, Long>(-1, 0)
    }
    context.sendJson(Json.encodeToString(MetaData(len, lastModified)))
}

fun putFileRoute(urlPath: String, context: HttpContext) {
    try {
        val file = File(urlPath.urlDecode())
        if (file.parentFile != null && file.parentFile?.exists() != true)
            Files.createDirectories(file.parentFile!!.toPath())

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

fun createDirectoryRoute(urlPath: String, context: HttpContext) {
    try {
        Files.createDirectory(Paths.get(urlPath.urlDecode()))
        context.sendNoContent()
    } catch (_: java.lang.Exception) {
        context.sendNotFound()
    }
}

fun deleteFileRoute(urlPath: String, context: HttpContext) {
    try {
        File(urlPath.urlDecode())
            .deleteRecursive()
        context.sendNoContent()
    } catch (_: java.lang.Exception) {
        context.sendNotFound()
    }
}

@Serializable
data class FileItem(val name: String, val isDirectory: Boolean, val size: Long, val isHidden: Boolean, val time: Long)

@Serializable
data class MetaData(val size: Long, val time: Long)

