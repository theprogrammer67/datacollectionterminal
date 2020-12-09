package ru.rarus.datacollectionterminal

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors

class RestServer {
    private var mHttpServer: HttpServer? = null
    private var _started = false
    val started: Boolean get() = (mHttpServer != null) && (_started)
    private val gson = Gson()

    fun start(port: Int) {
        try {
            mHttpServer = HttpServer.create(InetSocketAddress(port), 0)
            mHttpServer!!.executor = Executors.newCachedThreadPool()

            mHttpServer!!.createContext("/", rootHandler)
            mHttpServer!!.createContext("/index", rootHandler)
            // Handle /ping endpoint
            mHttpServer!!.createContext("/ping", pingHandler)
            mHttpServer!!.createContext("/test", testHandler)

            mHttpServer!!.start()

            _started = true
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun stop() {
        _started = false
        if (mHttpServer != null) {
            mHttpServer!!.stop(0)
        }
    }

    // Handler for root endpoint
    private val rootHandler = HttpHandler { exchange ->
        run {
            // Get request method
            when (exchange!!.requestMethod) {
                "GET" -> {
                    sendResponse(exchange, "Добро пожаловать!")
                }
            }
        }
    }

    // Handler for ping endpoint
    private val pingHandler = HttpHandler { exchange ->
        run {
            // Get request method
            when (exchange!!.requestMethod) {
                "GET" -> {
                    sendResponse(exchange, "true")
                }
            }
        }
    }

    // Handler for test endpoint
    private val testHandler = HttpHandler { exchange ->
        run {
            // Get request method
            when (exchange!!.requestMethod) {
                "GET" -> {
                    val data = TestData(123, "Test123")
                    sendResponse<TestData>(exchange, data)
                }
            }
        }
    }

    private fun streamToString(inputStream: InputStream): String {
        val s = Scanner(inputStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    private fun sendResponse(httpExchange: HttpExchange, responseText: String) {
        val rawBody = responseText.toByteArray(Charsets.UTF_8)
        httpExchange.responseHeaders.add("Content-Type","text/html")
        httpExchange.responseHeaders.add("charset","utf-8")
        httpExchange.sendResponseHeaders(200, rawBody.size.toLong())
        val os = httpExchange.responseBody
        os.write(rawBody)
        os.close()
    }

    private fun <T>sendResponse(httpExchange: HttpExchange, obj: T) {
        val jsonObj = gson.toJson(obj)
        val rawBody = jsonObj.toByteArray(Charsets.UTF_8)
        httpExchange.responseHeaders.add("Content-Type","text/JSON")
        httpExchange.responseHeaders.add("charset","utf-8")
        httpExchange.sendResponseHeaders(200, rawBody.size.toLong())
        val os = httpExchange.responseBody
        os.write(rawBody)
        os.close()
    }

}