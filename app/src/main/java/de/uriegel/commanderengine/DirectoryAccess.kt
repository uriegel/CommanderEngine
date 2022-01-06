package de.uriegel.commanderengine

import android.os.Environment
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import java.io.File

fun Route.testRoute() {
    route("/get") {
        get {
            val items = listOf<Item>(Item("Bild3.jpg", 23452),
                Item("Bild345.jpg", 3245345),
                Item("Bild333.jpg", 74556789)
            )
            val result = ItemResult("/home/uwe/Pictures", items)
            call.respond(result)
        }
    }
}

fun Route.getFilesRoute() {
    route("/getfiles") {
        post {
            val params = call.receive<GetFiles>()
            val path = "${Environment.getExternalStorageDirectory()}${params.path}"
            val directory = File(path)
            val items = directory.listFiles()
                ?.filterNotNull()
                ?.map {
                    File(it.name, it.isDirectory, it.length(), it.name.startsWith('.'), it.lastModified())
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
                FileInfo(path, file.length(), file.lastModified())
            }
            call.respond(infos)
        }
    }
}

data class ItemResult(val path: String, val items: List<Item>)
data class Item(val name: String, val size: Long)
data class GetFiles(val path: String)
data class GetFilesInfos(val files: List<String>)
data class File(val name: String, val isDirectory: Boolean, val size: Long, val isHidden: Boolean, val time: Long)
data class FileInfo(val file: String, val size: Long, val time: Long)