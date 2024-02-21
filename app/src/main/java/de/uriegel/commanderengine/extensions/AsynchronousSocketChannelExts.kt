package de.uriegel.commanderengine.extensions

import android.util.Log
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun AsynchronousSocketChannel.readAsync(buffer: ByteBuffer): Int = suspendCoroutine {
    this.read(buffer, null, object: CompletionHandler<Int,Void?> {
        override fun completed(count: Int, att: Void?) {
            it.resume(count)
        }
        override fun failed(exc: Throwable, att: Void?) {
            Log.d("TESTREC", "Error read")
        }
    })
}

suspend fun AsynchronousSocketChannel.writeAsync(buffer: ByteArray): Int = suspendCoroutine {
    this.write(ByteBuffer.wrap(buffer), null, object: CompletionHandler<Int,Void?> {
        override fun completed(count: Int, att: Void?) {
            it.resume(count)
        }
        override fun failed(exc: Throwable, att: Void?) {
            Log.d("TESTREC", "Error write")
        }
    })
}
