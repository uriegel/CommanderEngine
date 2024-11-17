package de.uriegel.commanderengine.httpserver

import android.util.Log
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Scanner

class HttpInputStream(private val rawHttpStream: InputStream) : InputStream() {
    fun nextLine(): String? {
        if (scanner == null) {
            Log.i("URIEGEL", "before read")
            pos = rawHttpStream.read(buffer)
            Log.i("URIEGEL", "read $pos")
            if (pos == -1)
                return null
            scanner = Scanner(ByteArrayInputStream(buffer))
        }

        return scanner!!.nextLine()
    }

    override fun read(): Int {
        TODO("Not yet implemented")
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        tailrec fun getHeaderEnd(startPos: Int): Int {
            val pos = buffer.drop(startPos).indexOf(10.toByte()) + startPos
            return if (buffer[pos + 2] == 10.toByte())
                pos + 3
            else
                getHeaderEnd(pos + 1)
        }

        if (posHeadersEnd == -1)
            posHeadersEnd = getHeaderEnd(0)

        val bufferRestLen = pos - posHeadersEnd
        return if (bufferRestLen > 0) {
            buffer.copyInto(b, 0, posHeadersEnd, pos)
            pos = posHeadersEnd
            bufferRestLen
        } else
            rawHttpStream.read(b, 0, len)
    }

    fun finished() {
        scanner = null
        pos = 0
        posHeadersEnd = -1
    }

    private val buffer = ByteArray(8192)
    private var pos = 0
    private var posHeadersEnd = -1
    private var scanner :Scanner? = null
}