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

        val affe = readTest(buffer)


        return scanner!!.nextLine()
    }

    fun readTest(bytes: ByteArray) {

        tailrec fun GetHeaderEnd(startPos: Int): Int {
            val pos = bytes.drop(startPos).indexOf(10.toByte()) + startPos
            return if (bytes[pos + 2] == 10.toByte())
                pos + 2
            else
                GetHeaderEnd(pos + 1)
        }

        val pos = GetHeaderEnd(0)
    }

    override fun read(): Int {
        TODO("Not yet implemented")
    }

    init {

    }

    private var pos = 0
    private var buffer = ByteArray(8192)
    private var scanner :Scanner? = null
}