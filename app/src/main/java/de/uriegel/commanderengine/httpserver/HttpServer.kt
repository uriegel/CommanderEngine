package de.uriegel.commanderengine.httpserver

import android.util.Log
import de.uriegel.commanderengine.extensions.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

class HttpServer(private val builder: Builder) {
    fun start(): HttpServer {
        running = true

        CoroutineScope(Dispatchers.IO).launch {
            while (running) {
                try {
                    val client = server.accept()
                    Log.i("URIEGEL", "New socket")
                    CoroutineScope(Dispatchers.IO).launch {
                        request(client)
                    }
                } catch (e: Exception) {
                    break
                }
            }
        }
        return this
    }

    fun stop() {
        running = false
        server.close()
    }

    private fun request(client: Socket) {
        val id = idSeed.addAndGet(1).toString()
        val istream = HttpInputStream(client.getInputStream())
        val ostream = BufferedOutputStream(client.getOutputStream())
        Log.i("URIEGEL", "New socket $id")

        tailrec fun nextRequest() {
            try {
                if (!running) {
                    client.close()
                    return
                }

                val possReq = istream.nextLine()
                if (possReq == null) {
                    client.close()
                    return
                }
                val req = possReq.split(' ')
                val method = req[0]
                val url = req[1]
                val protocol = req[2]
                val headers = readHeaderPart(istream)
                    .map {
                        val pairs = it.split(": ")
                        val p = Pair(pairs[0], pairs[1])
                        p
                    }
                    .toMap()
                Log.i("URIEGEL", "New request $id $url")
                if (method == "OPTIONS")
                    handleOptions(id, headers, ostream)
                else if (!route(method, headers, url, istream, ostream))
                    sendNotFound(ostream, headers)
                ostream.flush()
            } catch(e: Exception) {
                Log.i("URIEGEL", "exception $id ${e.message}")
                return
            } finally {
                istream.finished()
            }
            nextRequest()
        }
        nextRequest()
    }

    private fun route(method: String, headers: Map<String, String>, url: String,
                      httpInputStream: HttpInputStream, ostream: OutputStream): Boolean {
        var finished = false
        if (method == "GET") {
            finished = builder
                .routing
                ?.get
                ?.request(HttpContext(
                    url,
                    headers,
                    { sendJson(ostream, headers, it) },
                    { istream, size, filename, responseHeaders -> sendStream(ostream, headers, size,
                        filename ,istream, responseHeaders) },
                    { receivedStream -> postStream(headers, httpInputStream, receivedStream)},
                    { sendNotFound(ostream, headers) },
                    { sendNoContent(ostream, headers) }
                )) == null
        }
        if (!finished && method == "POST") {
            finished = builder
                .routing
                ?.post
                ?.request(HttpContext(
                    url,
                    headers,
                    { sendJson(ostream, headers, it) },
                    { istream, size, filename, responseHeaders -> sendStream(ostream, headers, size,
                        filename ,istream, responseHeaders) },
                    { receivedStream -> postStream(headers, httpInputStream, receivedStream)},
                    { sendNotFound(ostream, headers) },
                    { sendNoContent(ostream, headers) }
                )) == null
        }
        if (!finished && method == "PUT") {
            finished = builder
                .routing
                ?.put
                ?.request(HttpContext(
                    url,
                    headers,
                    { sendJson(ostream, headers, it) },
                    { istream, size, filename, responseHeaders -> sendStream(ostream, headers, size,
                        filename ,istream, responseHeaders) },
                    { receivedStream -> postStream(headers, httpInputStream, receivedStream)},
                    { sendNotFound(ostream, headers) },
                    { sendNoContent(ostream, headers) }
                )) == null
        }
        return finished
    }

    private fun sendJson(ostream: OutputStream, headers: Map<String, String>,
                                 json: String) =
        handleBytes(ostream, headers, "application/json", json.toByteArray())

    private fun sendStream(httpOutputStream: OutputStream, headers: Map<String, String>,
                           size: Long, fileName: String,
                           stream: InputStream, responseHeaders: MutableMap<String, String>) {
        responseHeaders["Content-Length"] = "$size"
        fileName.contentType()?.also {
            responseHeaders["Content-Type"] = it
        }

        val msg =
            "HTTP/1.1 200 OK\r\n" +
                    responseHeaders
                        .map { it.key + ": " + it.value}
                        .joinToString("\r\n") +
                    "\r\n\r\n"

        httpOutputStream.write(msg.toByteArray())

        val buffer = ByteArray(8192)
        tailrec fun sendBytes() {
            val length = stream.read(buffer)
            if (length > 0) {
                httpOutputStream.write(buffer, 0, length)
                sendBytes()
            }
        }
        sendBytes()
    }

    private fun postStream(headers: Map<String, String>, httpInputStream: HttpInputStream, outputStream: OutputStream) {
        val length = headers["Content-Length"]?.toInt() ?: 0

        val buffer = ByteArray(8192)
        tailrec fun readBuffer(len: Int) {
            val read = httpInputStream.read(buffer, 0, min(buffer.size, len))
            outputStream.write(buffer, 0, read)
            val left = len-read
            if (left > 0)
                readBuffer(left)
        }

        readBuffer(length)
    }

    private fun handleBytes(outputStream: OutputStream, headers: Map<String, String>,
            contentType: String, bytes: ByteArray) {
        val headerBytes = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: ${bytes.size}\r\n" +
                (headers["Origin"]?.let {
                    "Access-Control-Allow-Origin: $it\r\n"
                } ?: "") +
                "Content-Type: $contentType\r\n" +
                "\r\n"
        outputStream.write(headerBytes.toByteArray())
        outputStream.write(bytes)
    }

    private fun sendNotFound(ostream: OutputStream, headers: Map<String, String>) {
        val payload = """
            <html><head><meta http-equiv="Content-Type" content="text/html"/>
            <title>404 - File not found</title>
            <style>
            	body { font-family:message-box, Sans-Serif; font-size:10pt; }
            	h2 { font-weight:bold; font-size:14pt; color:#c03030; }
            	h3 { font-weight:bold; font-size:10pt;  }
            	a { color:#6666cc; margin-top:0.5em; text-decoration:none; }
            	a:hover { text-decoration:underline; }
            	a:focus { outline:none; }
            </style>
            </head><body><h2>File not found</h2>
            <div>
                Unable to find the specified file.
            </div>

            </body></html>
        """.trimIndent()

        val msg = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: ${payload.length}\r\n" +
                (headers["Origin"]?.let {
                    "Access-Control-Allow-Origin: $it\r\n"
                } ?: "") +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                payload
        ostream.write(msg.toByteArray())
    }

    private fun sendNoContent(ostream: OutputStream, headers: Map<String, String>) {
        val msg = "HTTP/1.1 204 No Content\r\n" +
                (headers["Origin"]?.let {
                    "Access-Control-Allow-Origin: $it\r\n"
                } ?: "") +
                "\r\n"
        ostream.write(msg.toByteArray())
    }

    private fun handleOptions(id: String, headers: Map<String, String>, ostream: OutputStream) {
        val responseHeaders = mutableMapOf<String, String>()
        builder.corsDomain?.let{
            responseHeaders["Access-Control-Allow-Origin"] = it
            responseHeaders["Access-Control-Allow-Headers"] = headers["Access-Control-Request-Headers"]!!
            responseHeaders["Access-Control-Allow-Method"] = headers["Access-Control-Request-Method"]!!
        }
        val msg =
            "HTTP/1.1 204 No Content\r\n" +
            responseHeaders
                .map { it.key + ": " + it.value}
                .joinToString("\r\n") +
            "\r\n"
        ostream.write(msg.toByteArray())
    }

    private fun readHeaderPart(istream: HttpInputStream) = sequence {
        while (true) {
            val line = istream.nextLine() ?: ""
            if (line.isEmpty())
                break
            yield(line)
        }
    }

    private val server: ServerSocket = ServerSocket(builder.port)

    companion object {
        private var idSeed = AtomicInteger(0)
    }

    private var running = false
}

data class HttpContext(
    val url: String,
    val headers: Map<String, String>,
    val sendJson: (json: String)->Unit,
    val sendStream: (stream: InputStream, size: Long, fileName: String,
                     headers: MutableMap<String, String>)->Unit,
    val postStream: (stream: OutputStream)->Unit,
    val sendNotFound: ()->Unit,
    val sendNoContent: ()->Unit)

