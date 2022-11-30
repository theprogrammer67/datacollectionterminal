package ru.rarus.datacollectionterminal.io.handlers

import android.database.sqlite.SQLiteConstraintException
import com.sun.net.httpserver.HttpHandler
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.io.*
import ru.rarus.datacollectionterminal.io.Errors.Companion.createHttpException
import java.net.URI


open class BasePathHandler(private val server: BaseRestServer, val path: String) {

    private val handler = HttpHandler { exchange ->
        val id = exchange.requestURI.getFileName()
        try {
            when (exchange!!.requestMethod) {
                "GET" -> {
                    var obj = onMethGet(id)
                    if (obj == null)
                        obj = Errors.makeNotFoundError()
                    server.sendResponse(exchange, obj)
                }
                "DELETE" -> {
                    try {
                        onMethDelete(id)
                        server.sendResponse(exchange, Errors.makeOkError())
                    } catch (e: SQLiteConstraintException) {
                        server.sendResponse(
                            exchange,
                            Errors.makeServerError(App.context.getString(R.string.error_delete_good))
                        )
                    }
                }
                "POST" -> {
                    val json = String(exchange.requestBody.readBytes(), Charsets.UTF_8)
                    if (json.isEmpty())
                        throw createHttpException(HTTP_CODE_BAD_REQUEST)
                    onMethPost(json, id)
                    server.sendResponse(exchange, Errors.makeOkError())
                }
                else -> {
                    server.sendResponse(
                        exchange,
                        Errors.makeNotImplementedError()
                    )
                }
            }
        } catch (e: HttpException) {
            val message: String = e.message ?: "Неизвестная ошибка"
            server.sendResponse(
                exchange, Errors.HandlerError(e.code, message)
            )
        } catch (e: Exception) {
            server.sendResponse(
                exchange, Errors.makeServerError(e.message)
            )
            return@HttpHandler
        }
    }

    init {
        server.httpServer?.createContext(path, handler)
    }


    open fun onMethGet(id: String): Any? {
        throw createHttpException(HTTP_CODE_NOT_ACCEPTABLE)
    }

    open fun onMethDelete(id: String) {
        throw createHttpException(HTTP_CODE_NOT_ACCEPTABLE)
    }

    open fun onMethPost(json: String, id: String) {
        throw createHttpException(HTTP_CODE_NOT_ACCEPTABLE)
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
