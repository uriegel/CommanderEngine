package de.uriegel.commanderengine

import android.os.Environment
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File
import java.lang.Exception

fun Route.getFilesRoute() {
    route("/remote/getfiles") {
        post {
            val params = call.receive<GetFiles>()
            val path = "${Environment.getExternalStorageDirectory()}${params.path}"
            val directory = File(path)
            val items = directory.listFiles()
                ?.filterNotNull()
                ?.map {
                    File(
                        it.name,
                        it.isDirectory,
                        it.length(),
                        it.name.startsWith('.'),
                        it.lastModified()
                    )
                }
            call.respond(Result.success(items ?: listOf<File>()).toQueryResult())
        }
    }
}

//TODO https://ktor.io/docs/partial-content.html#install_plugin
//TODO use get request

fun Route.getFileRoute() {
    route("/remote/getfile") {
        post {
            val params = call.receive<GetFiles>()
            val path = "${Environment.getExternalStorageDirectory()}${params.path}"
            val file = File(path)
            if (file.exists()) {
                call.response.header("Content-Disposition", "attachment; filename=\"${file.name}\"")
                call.response.header("x-file-date", "${file.lastModified()}")
                call.respondFile(file)
            }
            else
                call.respond(HttpStatusCode.NotFound)
        }
    }
}

fun Route.get() {
    route("/remote/{...}") {
        get {
            val file = File("${Environment.getExternalStorageDirectory()}${call.request.path().substring(7)}")
            if (file.exists()) {
                call.respondFile(file)
            } else
                call.respond(HttpStatusCode.NotFound)
        }
    }
}

fun Route.postFileRoute() {
    route("/remote/postfile") {
        post {
            withContext(Dispatchers.IO) {
                val file = File("${Environment.getExternalStorageDirectory()}${call.request.queryParameters["path"]!!}")
                call.receiveStream().copyTo(file.outputStream())
                val ft =
                    call
                        .request
                        .header("x-file-date")
                        ?.toLong()
                if (ft != null)
                    file.setLastModified(ft)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

fun Route.deleteFileRoute() {
    route("/remote/deletefile") {
        delete {
            withContext(Dispatchers.IO) {
                File("${Environment.getExternalStorageDirectory()}${call.request.queryParameters["path"]!!}").delete()
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

fun Route.getFilesInfosRoute() {
    route("/remote/getfilesinfos") {
        post {
            val params = call.receive<GetFilesInfos>()
            val infos = params.files.map {
                val path = "${Environment.getExternalStorageDirectory()}${it}"
                val file = File(path)
                if (file.exists())
                    FileInfo(true, it, file.length(), file.lastModified())
                else
                    FileInfo(false, it, file.length(), file.lastModified())
            }
            call.respond(infos)
        }
    }
}

data class QueryResult<T, TR>(
    val ok: T?,
    val error: TR?,
    val isError: Boolean?
)

fun <T> Result<T>.toQueryResult() =
    if (this.isSuccess)
        QueryResult<T, Exception>(this.getOrNull(), null, null)
    else
        QueryResult<T, Exception>(null, null, true)


data class GetFiles(val path: String)
data class GetFilesInfos(val files: List<String>)
data class File(val name: String, val isDirectory: Boolean, val size: Long, val isHidden: Boolean, val time: Long)
data class FileInfo(val exists: Boolean, val file: String, val size: Long, val time: Long)