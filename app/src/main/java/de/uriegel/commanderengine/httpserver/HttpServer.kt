package de.uriegel.commanderengine.httpserver

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.Scanner
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class HttpServer(port: Int) {
    fun start() {
        running = true
        CoroutineScope(Dispatchers.Default).launch {
            launch {
                while (running) {
                    Log.d("TAG", "before accept")
                    val channel = accept()
                    Log.d("TAG", "after accept")
                    launch {
                        request(channel)
                    }
                }
            }
        }
//        CoroutineScope(Dispatchers.Default).launch {
//            Log.d("TAG", "Launch accept loop")
//            launch {
//                while (running) {
//                    try {
//                        Log.d("TAG", "before accept")
//                        val client = server.accept()
//                        Log.d("TAG", "accept")
//                        launch {
//                            request(client)
//                        }
//                    } catch (e: Exception) {
//                        val a = 0
//                    }
//                }
//            }
//        }
    }

    private suspend fun request(channel: AsynchronousSocketChannel) {
        val buffer = ByteArray(8192)
        val count = read(channel, ByteBuffer.wrap(buffer))
        val input = Scanner(ByteArrayInputStream(buffer))

        val req = input.nextLine().split(' ')
        val method = req[0]
        val url = req[1]
        val protocol = req[2]
        val headers = readHeaderPart(input)
            .map {
                val pairs = it.split(": ")
                val p =  Pair(pairs[0], pairs[1])
                p
            }
            .toMap()
        val test = headers
//        if (method == "OPTIONS")
//            handleOptions(headers, ostream)
//        else
//            handleRequest(headers, ostream)
    }

    private suspend fun accept(): AsynchronousSocketChannel = suspendCoroutine {
        listener.accept(null, object: CompletionHandler<AsynchronousSocketChannel,Void?> {
            override fun completed(ch: AsynchronousSocketChannel, att: Void?) {
                it.resume(ch)
            }
            override fun failed(exc: Throwable, att: Void?) { }
        })
    }

    private suspend fun read(channel: AsynchronousSocketChannel, buffer: ByteBuffer): Int = suspendCoroutine {
        channel.read(buffer, null, object: CompletionHandler<Int,Void?> {
            override fun completed(count: Int, att: Void?) {
                it.resume(count)
            }
            override fun failed(exc: Throwable, att: Void?) { }
        })
    }

    fun stop() {
        running = false
        //server.close()
    }


    private fun request(socket: Socket) {





        val istream = Scanner(socket.getInputStream())
        val ostream = BufferedOutputStream(socket.getOutputStream())
        tailrec fun nextRequest() {
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
            nextRequest()
        }

        nextRequest()
    }

    // TODO Stop (start stop service several times)
    // TODO KeepAlive
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

    val listener = AsynchronousServerSocketChannel
        .open()
        .bind(InetSocketAddress(port))

    private var running = false
}