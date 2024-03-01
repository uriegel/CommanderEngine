package de.uriegel.commanderengine.httpserver

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Scanner

class HttpInputStream(private val rawHttpStream: InputStream) : InputStream() {
    fun nextLine(): String {
        if (scanner == null) {
            pos = rawHttpStream.read(buffer)
            scanner = Scanner(ByteArrayInputStream(buffer))
        }

        return scanner!!.nextLine()
    }

    override fun read(): Int {
        TODO("Not yet implemented")
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        tailrec fun GetHeaderEnd(startPos: Int): Int {
            val pos = buffer.drop(startPos).indexOf(10.toByte()) + startPos
            return if (buffer[pos + 2] == 10.toByte())
                pos + 3
            else
                GetHeaderEnd(pos + 1)
        }

        if (posHeadersEnd == -1)
            posHeadersEnd = GetHeaderEnd(0)

        val bufferRestLen = pos - posHeadersEnd
        return if (bufferRestLen > 0) {
            buffer.copyInto(b, 0, posHeadersEnd, pos)
            pos = posHeadersEnd
            bufferRestLen
        } else
            rawHttpStream.read(b, 0, len)
    }

    private var pos = 0
    private var posHeadersEnd = -1
    private var buffer = ByteArray(8192)
    private var scanner :Scanner? = null
}