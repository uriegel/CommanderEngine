package de.uriegel.commanderengine

import android.os.Environment
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun getFilesRoute(urlPath: String): String {
    val path = "${Environment.getExternalStorageDirectory()}${urlPath}"
    val directory = File(path)
    return Json.encodeToString(
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
                ?: listOf<FileItem>())
}

////TODO use get request
//
//fun Route.getFileRoute() {
//    route("/remote/getfile") {
//        post {
//            val params = call.receive<GetFiles>()
//            val path = "${Environment.getExternalStorageDirectory()}${params.path}"
//            val file = File(path)
//            if (file.exists()) {
//                call.response.header("Content-Disposition", "attachment; filename=\"${file.name}\"")
//                call.response.header("x-file-date", "${file.lastModified()}")
//                call.respondFile(file)
//            }
//            else
//                call.respond(HttpStatusCode.NotFound)
//        }
//    }
//}
//
//fun Route.get() {
//    route("/remote/{...}") {
//        get {
//            val file = File("${Environment.getExternalStorageDirectory()}${call.request.path().substring(7)}")
//            if (file.exists()) {
//                call.respondFile(file)
//            } else
//                call.respond(HttpStatusCode.NotFound)
//        }
//    }
//}
//
//fun Route.postFileRoute() {
//    route("/remote/postfile") {
//        post {
//            withContext(Dispatchers.IO) {
//                try {
//                    val file =
//                        File("${Environment.getExternalStorageDirectory()}${call.request.queryParameters["path"]!!}")
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
//                } catch (_: java.lang.Exception) {
//                }
//
//            }
//        }
//    }
//}
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
//fun Route.getFilesInfosRoute() {
//    route("/remote/getfilesinfos") {
//        post {
//            val params = call.receive<GetFilesInfos>()
//            val infos = params.files.map {
//                val path = "${Environment.getExternalStorageDirectory()}${it}"
//                val file = File(path)
//                if (file.exists())
//                    FileInfo(true, it, file.length(), file.lastModified())
//                else
//                    FileInfo(false, it, file.length(), file.lastModified())
//            }
//            call.respond(infos)
//        }
//    }
//}
//
//data class QueryResult<T, TR>(
//    val ok: T?,
//    val error: TR?,
//    val isError: Boolean?
//)
//
//fun <T> Result<T>.toQueryResult() =
//    if (this.isSuccess)
//        QueryResult<T, Exception>(this.getOrNull(), null, null)
//    else
//        QueryResult<T, Exception>(null, null, true)


//data class GetFiles(val path: String)
//data class GetFilesInfos(val files: List<String>)
@Serializable
data class FileItem(val name: String, val isDirectory: Boolean, val size: Long, val isHidden: Boolean, val time: Long)
//data class FileInfo(val exists: Boolean, val file: String, val size: Long, val time: Long)