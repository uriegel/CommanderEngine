package de.uriegel.commanderengine

import android.os.Environment
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import java.io.File

fun Route.getFilesRoute() {
    route("/getfiles") {
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
            call.respond(items ?: listOf<File>())
        }
    }
}

fun Route.getFileRoute() {
    route("/getfile") {
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

fun Route.getFilesInfosRoute() {
    route("/getfilesinfos") {
        post {
            val params = call.receive<GetFilesInfos>()
            val infos = params.files.map {
                val path = "${Environment.getExternalStorageDirectory()}${it}"
                val file = File(path)
                FileInfo(it, file.length(), file.lastModified())
            }
            call.respond(infos)
        }
    }
}

data class GetFiles(val path: String)
data class GetFilesInfos(val files: List<String>)
data class File(val name: String, val isDirectory: Boolean, val size: Long, val isHidden: Boolean, val time: Long)
data class FileInfo(val file: String, val size: Long, val time: Long)