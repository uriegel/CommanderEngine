package de.uriegel.commanderengine

import android.os.Environment
import de.uriegel.commanderengine.extensions.cutAt
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

fun getFileRoute(urlPath: String, context: HttpContext) {
    val file = File("${Environment.getExternalStorageDirectory()}${urlPath}".cutAt('?'))
    if (file.exists()) {
        val headers = mutableMapOf(
            "Content-Disposition" to "attachment; filename=\"${file.name}\"",
            "x-file-date" to "${file.lastModified()}")
        context.sendStream(file.inputStream(), file.length(), file.name, headers)
    }
    else
        context.sendNotFound()
}

fun postFileRoute(urlPath: String, context: HttpContext) {
    try {
        val file = File("${Environment.getExternalStorageDirectory()}${urlPath}".cutAt('?'))

        context.postStream(file.outputStream())
//                    call.receiveChannel().copyAndClose(file.writeChannel())
//                    //call.receiveStream().copyTo(file.outputStream(), 8192)
//                    val ft =
//                        call
//                            .request
//                            .header("x-file-date")
//                            ?.toLong()
//                    if (ft != null)
//                        file.setLastModified(ft)
//                    //file.renameTo(File("${Environment.getExternalStorageDirectory()}${call.request.queryParameters["path"]!!}"))
//                    call.respond(HttpStatusCode.OK)

    } catch (_: java.lang.Exception) {
        context.sendNotFound()
    }
}
//
//fun Route.deleteFileRoute() {
//    route("/remote/deletefile") {
//        delete {
//            withContext(Dispatchers.IO) {
//                File("${Environment.getExternalStorageDirectory()}${call.request.queryParameters["path"]!!}").deleteRecursive()
//                call.respond(HttpStatusCode.OK)
//            }
//        }
//    }
//}
//
//data class GetFiles(val path: String)
//data class GetFilesInfos(val files: List<String>)
@Serializable
data class FileItem(val name: String, val isDirectory: Boolean, val size: Long, val isHidden: Boolean, val time: Long)
//data class FileInfo(val exists: Boolean, val file: String, val size: Long, val time: Long)

@Serializable
data class ResultItem<T>(val ok: T?) {

    companion object {
        fun <T> ok(value: Result<T>): ResultItem<T> =
            ResultItem(value.getOrNull())
    }
}

