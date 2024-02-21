package de.uriegel.commanderengine.httpserver

import android.util.Log
import de.uriegel.commanderengine.extensions.readAsync
import de.uriegel.commanderengine.extensions.writeAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.Scanner
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class HttpServer(private val builder: Builder) {
    fun start(): HttpServer {
        listener = AsynchronousServerSocketChannel
            .open()
            .bind(InetSocketAddress(builder.port))

        running = true
        CoroutineScope(Dispatchers.Default).launch {
            while (running) {
                Log.d("TESTREC", "before accept")
                val channel = accept()
                Log.d("TESTREC", "after accept")
                CoroutineScope(Dispatchers.Default).launch {
                    request(channel)
                }
            }
        }
        return this
    }

    fun stop() {
        running = false
        listener?.close()
    }

    private suspend fun accept(): AsynchronousSocketChannel = suspendCoroutine {
        listener?.accept(null, object: CompletionHandler<AsynchronousSocketChannel,Void?> {
            override fun completed(ch: AsynchronousSocketChannel, att: Void?) {
                it.resume(ch)
            }
            override fun failed(exc: Throwable, att: Void?) {
                Log.d("TESTREC", "Error accept")
            }
        })
    }

    private suspend fun request(channel: AsynchronousSocketChannel) {
        val buffer = ByteArray(8192)
        val id = idSeed.addAndGet(1).toString()
        tailrec suspend fun request() {
            val count = channel.readAsync(ByteBuffer.wrap(buffer))
            Log.d("TESTREC", "$id After read: $count")

            if (!running || count == -1) {
                channel.close()
                return
            }

            val input = Scanner(ByteArrayInputStream(buffer))
            val req = input.nextLine().split(' ')
            val method = req[0]
            val url = req[1]
            val headers = readHeaderPart(input)
                .map {
                    val pairs = it.split(": ")
                    val p = Pair(pairs[0], pairs[1])
                    p
                }
                .toMap()
            if (method == "OPTIONS")
                handleOptions(id, headers, channel)
            else
                handleRequest(id, headers, channel)
            request()
        }
        request()
    }

    private suspend fun handleRequest(id: String, headers: Map<String, String>, channel: AsynchronousSocketChannel) {
        Log.d("TESTREC", "$id req")
        val msg = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 18\r\n" +
                (headers["Origin"]?.let {
                    "Access-Control-Allow-Origin: $it\r\n"
                } ?: "") +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                "Das is der Payload"
        channel.writeAsync(msg.toByteArray())
        Log.d("TESTREC", "$id req finished")
    }

    private suspend fun handleOptions(id: String, headers: Map<String, String>, channel: AsynchronousSocketChannel) {
        Log.d("TESTREC", "$id options")
        val responseHeaders = mutableMapOf<String, String>()
        builder.corsDomain?.let{
            responseHeaders["Access-Control-Allow-Origin"] = it
            responseHeaders["Access-Control-Allow-Headers"] = headers["Access-Control-Request-Headers"]!!
            responseHeaders["Access-Control-Allow-Method"] = headers["Access-Control-Request-Method"]!!
        }
        responseHeaders["Content-Length"] = "0"
        val msg =
            "HTTP/1.1 200 OK\r\n" +
            responseHeaders
                .map { it.key + ": " + it.value}
                .joinToString("\r\n") +
            "\r\n\r\n"
        channel.writeAsync(msg.toByteArray())
        Log.d("TESTREC", "$id options finished")
    }

    // TODO send content-type in answer
    private fun readHeaderPart(istream: Scanner) = sequence {
        while (true) {
            val line = istream.nextLine()
            if (line.isEmpty())
                break
            yield(line)
        }
    }

    companion object {
        private var idSeed = AtomicInteger(0)
    }

    private var listener: AsynchronousServerSocketChannel? = null
    private var running = false
}