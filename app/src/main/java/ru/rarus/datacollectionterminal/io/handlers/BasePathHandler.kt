package ru.rarus.datacollectionterminal.io.handlers

import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import ru.rarus.datacollectionterminal.db.entities.DocumentHeader
import ru.rarus.datacollectionterminal.io.RestServer
import ru.rarus.datacollectionterminal.io.getFileName
import ru.rarus.datacollectionterminal.io.Errors


open class BasePathHandler(private val server: RestServer, val path: String) {

    private val handler = HttpHandler { exchange ->
        val id = exchange.requestURI.getFileName()
        try {
            when (exchange!!.requestMethod) {
                "GET" -> {
                    var obj = onMethGet(exchange, id)
                    if (obj == null)
                        obj = Errors.makeNotFoundError()
                    server.sendResponse(exchange, obj)
                }
                "DELETE" -> {
                    onMethDelete(exchange, id)
                }
                "POST" -> {
                    onMethPost(exchange, id)
                }
                else -> {
                    server.sendResponse(
                        exchange,
                        Errors.makeNotImplementedError()
                    )
                }
            }
        } catch (e: Exception) {
            server.sendResponse(
                exchange, Errors.makeServerError(e.message)
            )
            return@HttpHandler
        }
    }

    init {
        server.mHttpServer?.createContext(path, handler)
    }


    open fun onMethGet(exchange: HttpExchange, id: String): Any? {
        return null
    }

    open fun onMethDelete(exchange: HttpExchange, id: String) {

    }

    open fun onMethPost(exchange: HttpExchange, id: String) {

    }

}