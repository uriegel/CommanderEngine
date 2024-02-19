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
            while (running) {
                Log.d("TAG", "before accept")
                val channel = accept()
                Log.d("TAG", "after accept")
                launch {
                    request(channel)
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

    fun stop() {
        running = false
        //server.close()
    }

    private tailrec suspend fun request(channel: AsynchronousSocketChannel) {
        val buffer = ByteArray(8192)
        read(channel, ByteBuffer.wrap(buffer))
        val input = Scanner(ByteArrayInputStream(buffer))

        val req = input.nextLine().split(' ')
        val method = req[0]
        val url = req[1]
        val headers = readHeaderPart(input)
            .map {
                val pairs = it.split(": ")
                val p =  Pair(pairs[0], pairs[1])
                p
            }
            .toMap()
        if (method == "OPTIONS")
            handleOptions(headers, channel)
        else
            handleRequest(headers, channel)
        request(channel)
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

    private suspend fun write(channel: AsynchronousSocketChannel, buffer: ByteArray): Int = suspendCoroutine {
        channel.write(ByteBuffer.wrap(buffer), null, object: CompletionHandler<Int,Void?> {
            override fun completed(count: Int, att: Void?) {
                it.resume(count)
            }
            override fun failed(exc: Throwable, att: Void?) { }
        })
    }

//    private fun request(socket: Socket) {
//
//
//
//
//
//        val istream = Scanner(socket.getInputStream())
//        val ostream = BufferedOutputStream(socket.getOutputStream())
//        tailrec fun nextRequest() {
//            val req = istream.nextLine().split(' ')
//            val method = req[0]
//            val url = req[1]
//            val protocol = req[2]
//            val headers = readHeaderPart(istream)
//                .map {
//                    val pairs = it.split(": ")
//                    val p =  Pair(pairs[0], pairs[1])
//                    p
//                }
//                .toMap()
//            if (method == "OPTIONS")
//                handleOptions(headers, ostream)
//            else
//                handleRequest(headers, ostream)
//            nextRequest()
//        }
//
//        nextRequest()
//    }

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

    private suspend fun handleRequest(headers: Map<String, String>, channel: AsynchronousSocketChannel) {
        val msg = "HTTP/1.1 200 OK\r\n" +
                 "Content-Length: 18\r\n" +

                 "Access-Control-Allow-Origin: http://localhost:5173\r\n" +


                 "Content-Type: application/json\r\n" +
                 "\r\n" +
                 "Das is der Payload"
        write(channel, msg.toByteArray())
    }

    private suspend fun handleOptions(headers: Map<String, String>, channel: AsynchronousSocketChannel) {
        val responseHeaders = mutableMapOf<String, String>()
        responseHeaders["Access-Control-Allow-Origin"] = "http://localhost:5173"
        responseHeaders["Access-Control-Allow-Headers"] = headers["Access-Control-Request-Headers"]!!
        responseHeaders["Access-Control-Allow-Method"] = headers["Access-Control-Request-Method"]!!
        val msg = "HTTP/1.1 200 OK\r\n" +
                    responseHeaders
                    .map { it.key + ": " + it.value}
                    .joinToString("\r\n") +
                "\r\n\r\n"
        write(channel, msg.toByteArray())
    }

    val listener = AsynchronousServerSocketChannel
        .open()
        .bind(InetSocketAddress(port))

    private var running = false
}