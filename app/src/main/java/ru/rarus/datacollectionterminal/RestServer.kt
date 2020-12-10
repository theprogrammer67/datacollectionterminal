package ru.rarus.datacollectionterminal

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.URI
import java.util.*
import java.util.concurrent.Executors
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class RestServer {
    private var mHttpServer: HttpServer? = null
    private var _started = false
    val started: Boolean get() = (mHttpServer != null) && (_started)
    private val gson = Gson()
    private val handlers = Handlers(this)

    fun start(port: Int) {
        try {
            mHttpServer = HttpServer.create(InetSocketAddress(port), 0)
            mHttpServer!!.executor = Executors.newCachedThreadPool()
            addHandlers()
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

    private fun streamToString(inputStream: InputStream): String {
        val s = Scanner(inputStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    fun sendResponse(httpExchange: HttpExchange, responseText: String) {
        val rawBody = responseText.toByteArray(Charsets.UTF_8)
        httpExchange.responseHeaders.add("Content-Type", "text/html")
        httpExchange.responseHeaders.add("charset", "utf-8")
        httpExchange.responseHeaders.add("Accept-Language", "ru-RU")
        httpExchange.sendResponseHeaders(200, rawBody.size.toLong())
        val os = httpExchange.responseBody
        os.write(rawBody)
        os.close()
    }

    fun <T> sendResponse(httpExchange: HttpExchange, obj: T) {
        val jsonObj = gson.toJson(obj)
        val rawBody = jsonObj.toByteArray(Charsets.UTF_8)
        httpExchange.responseHeaders.add("Content-Type", "text/JSON")
        httpExchange.responseHeaders.add("charset", "utf-8")
        httpExchange.sendResponseHeaders(200, rawBody.size.toLong())
        val os = httpExchange.responseBody
        os.write(rawBody)
        os.close()
    }

    private fun addHandlers() {
        val handlerProps =
            Handlers::class.memberProperties.filter { it.findAnnotation<RequestHandler>() != null }
        handlerProps.forEach { handler ->
            mHttpServer!!.createContext(
                handler.findAnnotation<RequestHandler>()!!.path,
                handler.get(handlers) as HttpHandler
            )
        }
    }
}

fun URI.getFileName(): String {
    var _path = this.path
    while (_path.startsWith("/")) _path = _path.substring(1)
    while (_path.endsWith("/")) _path = _path.substring(0, _path.length - 1)

    val segments: List<String> = _path.split("/")
    return if (segments.size > 1)
        segments[1]
    else
        ""
}

//---------- Endpoint handlers----------
class Handlers(private val server: RestServer) {

    // root endpoint
    @RequestHandler("/")
    val rootHandler = HttpHandler { exchange ->
        when (exchange!!.requestMethod) {
            "GET" -> {
                val htmlResponse =
                    "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /></head>" +
                            "<body>Добро пожаловать!</body></html>"
                server.sendResponse(exchange, htmlResponse)
            }
        }
    }

    // test endpoint
    @RequestHandler("/test")
    val testHandler = HttpHandler { exchange ->
        when (exchange!!.requestMethod) {
            "GET" -> {
                val data = TestData(exchange.requestURI.getFileName(), 123, "Test123")
                server.sendResponse<TestData>(exchange, data)
            }
        }
    }
}

annotation class RequestHandler(val path: String)