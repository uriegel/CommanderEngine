package de.uriegel.commanderengine.httpserver

import java.net.ServerSocket
import kotlinx.coroutines.*
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.net.Socket
import java.util.Scanner

class HttpServer(port: Int) {

    private val server: ServerSocket

    fun start() {
        CoroutineScope(Dispatchers.Default).async {
            launch() {
                while (true) {
                    val client = server.accept()
                    launch(Dispatchers.Default) {
                        request(client)
                    }
                }
            }
        }
    }

    private fun request(socket: Socket) {
        val istream = Scanner(socket.getInputStream())
        val ostream = BufferedOutputStream(socket.getOutputStream())
        val req = istream.nextLine().split(' ')
        val method = req[0]
        val url = req[1]
        val protocol = req[2]
        val headers = readHeaderPart(istream)
                .map {
                    val pairs = it.split(": ")
                    val p =  Pair(pairs[0], pairs[1])
                    p
                }
                .toMap()
        if (method == "OPTIONS")
            handleOptions(headers, ostream)
        else
            handleRequest(headers, ostream)
    }

    // TODO Stop (start stop service several times)
    // TODO config allowed origin
    // TODO send origin back in answer if origin is the configured value
    // TODO send content-type in answer
    private fun readHeaderPart(istream: Scanner) = sequence {
        while (true) {
            val line = istream.nextLine()
            if (line.isEmpty())
                break
            yield(line)
        }
    }

    private fun handleRequest(headers: Map<String, String>, ostream: OutputStream) {
        ostream.write("HTTP/1.1 200 OK\r\n".toByteArray())
        ostream.write("Content-Length: 18\r\n".toByteArray())


        ostream.write("Access-Control-Allow-Origin: http://localhost:5173\r\n".toByteArray())


        ostream.write("Content-Type: application/json\r\n".toByteArray())
        ostream.write("\r\n".toByteArray())
        ostream.write("Das is der Payload".toByteArray())
        ostream.flush()
    }

    private fun handleOptions(headers: Map<String, String>, ostream: OutputStream) {
        val responseHeaders = mutableMapOf<String, String>()
        responseHeaders["Access-Control-Allow-Origin"] = "http://localhost:5173"
        responseHeaders["Access-Control-Allow-Headers"] = headers["Access-Control-Request-Headers"]!!
        responseHeaders["Access-Control-Allow-Method"] = headers["Access-Control-Request-Method"]!!
        ostream.write("HTTP/1.1 200 OK\r\n".toByteArray())
        ostream.write(responseHeaders
            .map { it.key + ": " + it.value}
            .joinToString("\r\n")
            .toByteArray())
        ostream.write("\r\n\r\n".toByteArray())
        ostream.flush()


//        headers.forEach {
//                Log.d("TAG", "${it.key} - ${it.value}")
//            }
    }


    init {
        server = ServerSocket(port)
    }


}