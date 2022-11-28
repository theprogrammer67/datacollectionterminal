package ru.rarus.datacollectionterminal.io

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.*
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.SERVER_PORT
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.URI
import java.util.*
import java.util.concurrent.Executors
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

annotation class RequestHandler(val path: String)
annotation class PathHandler

class RestServer {
    private var mHttpServer: HttpServer? = null
    val state = MutableLiveData<Boolean>()
    private val gson = Gson()
    private val handlers = Handlers(this)
    private var serverPort = SERVER_PORT
    val serverStarted get() = mHttpServer != null

    // Path handlers (endpoints)
    @PathHandler
    val classDocumentsHandler = DocumentsPathHandler::class

    private fun startServer(port: Int) {
        if ((mHttpServer == null) || (serverPort != port)) {
            stopServer()
            serverPort = port
            mHttpServer = HttpServer.create(InetSocketAddress(serverPort), 0)
            mHttpServer!!.executor = Executors.newCachedThreadPool()
            addHandlers()
            mHttpServer!!.start()
        }
    }

    private fun stopServer() {
        mHttpServer?.stop(0)
        mHttpServer = null
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun start(port: Int) {
        GlobalScope.launch {
            try {
                startServer(port)
                state.postValue(true)
            } catch (e: Exception) {
                stopServer()
                state.postValue(false)
                withContext(Dispatchers.Main) { App.showMessage(e.message) }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun stop() {
        GlobalScope.launch {
            state.postValue(false)
            stopServer()
        }
    }

    private fun streamToString(inputStream: InputStream): String {
        val s = Scanner(inputStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    fun sendResponse(httpExchange: HttpExchange, responseText: String, code: Int = 200) {
        val rawBody = responseText.toByteArray(Charsets.UTF_8)
        httpExchange.responseHeaders.add("Content-Type", "text/html")
        httpExchange.responseHeaders.add("charset", "utf-8")
        httpExchange.responseHeaders.add("Accept-Language", "ru-RU")
        httpExchange.sendResponseHeaders(code, rawBody.size.toLong())
        val os = httpExchange.responseBody
        os.write(rawBody)
        os.close()
    }

    fun <T> sendResponse(httpExchange: HttpExchange, obj: T) {
        val jsonObj = gson.toJson(obj)
        val rawBody = jsonObj.toByteArray(Charsets.UTF_8)
        httpExchange.responseHeaders.add("Content-Type", "application/json")
        httpExchange.responseHeaders.add("charset", "utf-8")
        val code = if (obj is Handlers.HandlerError) (obj as Handlers.HandlerError).code else 200
        httpExchange.sendResponseHeaders(code, rawBody.size.toLong())
        val os = httpExchange.responseBody
        os.write(rawBody)
        os.close()
    }

    private fun addHandlers() {
        val props =
            this.javaClass.kotlin.memberProperties.filter { it.findAnnotation<PathHandler>() != null }
        props.forEach { prop ->
            val handlerClass  = prop.get(this) as KClass<BasePathHandler>
            val instance = handlerClass.primaryConstructor!!.call(this)
        }

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

