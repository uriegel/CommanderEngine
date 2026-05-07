package de.uriegel.commanderengine

import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import de.uriegel.commanderengine.extensions.deleteRecursive
import de.uriegel.commanderengine.extensions.urlDecode
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.delete
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveStream
import io.ktor.server.response.respondFile
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.text.toLong

suspend fun getFileRoute(call: ApplicationCall, urlPath: String, download: Boolean) {
    val file = File(urlPath.urlDecode())
    if (file.exists() && file.isFile) {
        val contentType = ContentType.parse(Files.probeContentType(file.toPath()) ?: "application/octet-stream")

        call.response.headers.append("x-file-date", "${file.lastModified()}")
        if (download) {
            call.response.headers.append(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(
                    ContentDisposition.Parameters.FileName,
                    file.name
                ).toString()
            )
        }
        call.response.headers.append(HttpHeaders.ContentType, contentType.toString())
        call.respondFile(file)
        //return context.sendStream(file.inputStream(), file.length(), file.name, headers)
    }
    else {
        call.respond(HttpStatusCode.NotFound)
        //return@get
    }
}

fun startKtorServer(context: Context, port: Int = 8080): ApplicationEngine {
    val server = embeddedServer(CIO, port = port) {
        install(ContentNegotiation) {
            json() // kotlinx.serialization json
        }

        routing {
            // GET /items -> returns JSON list
            get("/getfiles/{param...}") {
                val urlPath = call.parameters.getAll("param")?.joinToString("/") ?: ""
                val result = if (urlPath == "" || urlPath == "/") {
                    (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        (context
                            .getSystemService(Context.STORAGE_SERVICE) as StorageManager)
                            .storageVolumes
                            .map {
                                it.directory?.path
                            }
                    else
                        context.getExternalFilesDirs(null)
                            .map {
                                it.absolutePath
                            })
                        .map {
                            FileItem(it ?: "", true, 0, it?.startsWith('.') == true, 0)
                        }
                }
                else
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
                        ?: listOf()

                call.respond(result)
            }
            get("/getfile/{param...}") {
                val urlPath = call.parameters.getAll("param")?.joinToString("/") ?: ""
                getFileRoute(call, urlPath, false)
            }
            get("/downloadfile/{param...}") {
                val urlPath = call.parameters.getAll("param")?.joinToString("/") ?: ""
                getFileRoute(call, urlPath, true)
            }
            get("/metadata/{param...}") {
                val urlPath = call.parameters.getAll("param")?.joinToString("/") ?: ""
                val file = File(urlPath.urlDecode())

                val (len, lastModified) = if (file.exists()) {
                    Pair(file.length(), file.lastModified())
                } else {
                    Pair<Long, Long>(-1, 0)
                }
                call.respond(Json.encodeToString(MetaData(len, lastModified)))
            }
            put("/putfile/{param...}") {
                val urlPath = call.parameters.getAll("param")?.joinToString("/") ?: ""
                try {
                    val file = File(urlPath.urlDecode())
                    if (file.parentFile != null && file.parentFile?.exists() != true)
                        Files.createDirectories(file.parentFile!!.toPath())

                    val httpStream = call.receiveStream()
                    httpStream.copyTo(file.outputStream())
                    call.request
                        .headers
                        .get("x-file-date")
                        ?.toLong()
                        ?.also { file.setLastModified(it) }
                    //file.renameTo(File("${Environment.getExternalStorageDirectory()}${call.request.queryParameters["path"]!!}"))
                    call.respond(HttpStatusCode.NoContent)

                } catch (_: java.lang.Exception) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Not found"))
                }
            }
            post("/createdirectory/{param...}") {
                val urlPath = call.parameters.getAll("param")?.joinToString("/") ?: ""
                try {
                    Files.createDirectory(Paths.get(urlPath.urlDecode()))
                    call.respond(HttpStatusCode.NoContent)
                } catch (_: java.lang.Exception) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Not found"))
                }
            }
            delete("/deletefile/{param...}") {
                val urlPath = call.parameters.getAll("param")?.joinToString("/") ?: ""
                try {
                    File(urlPath.urlDecode())
                        .deleteRecursive()
                    call.respond(HttpStatusCode.NoContent)
                } catch (_: java.lang.Exception) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Not found"))
                }
            }
        }
    }

    // Run server in background
    CoroutineScope(Dispatchers.IO).launch {
        server.start(wait = true)
    }

    return server
}

fun stopKtorServer(server: ApplicationEngine) {
    server.stop(gracePeriodMillis = 500, timeoutMillis = 1000)
}

@Serializable
data class FileItem(val name: String, val isDirectory: Boolean, val size: Long, val isHidden: Boolean, val time: Long)

@Serializable
data class MetaData(val size: Long, val time: Long)
