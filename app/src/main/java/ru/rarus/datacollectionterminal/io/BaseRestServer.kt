package ru.rarus.datacollectionterminal.io

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.*
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.SERVER_PORT
import ru.rarus.datacollectionterminal.io.handlers.BasePathHandler
import java.io.InputStream
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

annotation class PathHandler

open class BaseRestServer {
    var httpServer: HttpServer? = null
    val state = MutableLiveData<Boolean>()
    private val gson = Gson()
    private val handlers: MutableList<BasePathHandler> = ArrayList()
    private var serverPort = SERVER_PORT
    val serverStarted get() = httpServer != null

    open fun beforeStart() {
        addHandlers()
    }

    private fun startServer(port: Int) {
        if ((httpServer == null) || (serverPort != port)) {
            stopServer()

            serverPort = port
            httpServer = HttpServer.create(InetSocketAddress(serverPort), 0)
            httpServer!!.executor = Executors.newCachedThreadPool()

            beforeStart()
            httpServer!!.start()
        }
    }

    private fun stopServer() {
        httpServer?.stop(0)
        httpServer = null
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

    fun sendResponse(httpExchange: HttpExchange, obj: Any) {
        val jsonObj = gson.toJson(obj)
        val rawBody = jsonObj.toByteArray(Charsets.UTF_8)
        httpExchange.responseHeaders.add("Content-Type", "application/json")
        httpExchange.responseHeaders.add("charset", "utf-8")
        val code = if (obj is Errors.HandlerError) (obj as Errors.HandlerError).code else 200
        httpExchange.sendResponseHeaders(code, rawBody.size.toLong())
        val os = httpExchange.responseBody
        os.write(rawBody)
        os.close()
    }

    private fun addHandlers() {
        handlers.clear()

        val props =
            this.javaClass.kotlin.memberProperties.filter { it.findAnnotation<PathHandler>() != null }
        props.forEach { prop ->
            val handlerClass = prop.get(this) as KClass<*>
            val instance = handlerClass.primaryConstructor!!.call(this)
            handlers.add(instance as BasePathHandler)
        }
    }
}
