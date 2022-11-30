package ru.rarus.datacollectionterminal.io

import android.content.pm.PackageInfo
import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sun.net.httpserver.HttpHandler
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.BuildConfig
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.db.entities.ViewDocument
import ru.rarus.datacollectionterminal.db.entities.ViewGood
import ru.rarus.datacollectionterminal.db.models.DocumentModel
import ru.rarus.datacollectionterminal.db.models.GoodModel


class Handlers(private val server: BaseRestServer) {

    data class HandlerError(val code: Int, val message: String)

    private fun makeOkError(): HandlerError {
        return HandlerError(200, "Метод выполнен успешно")
    }

    private fun makeNotImplementedError(): HandlerError {
        return HandlerError(406, "Метод не поддерживается")
    }

    private fun makeNotFoundError(): HandlerError {
        return HandlerError(404, "Ресурс не найден")
    }

    private fun makeBadRequestError(): HandlerError {
        return HandlerError(400, "Некорректный запрос")
    }

    private fun makeServerError(text: String?): HandlerError {
        val errorDescr = text ?: "Неизвестная ошибка"
        return HandlerError(500, "Ошибка сервера: $errorDescr")
    }

    // root endpoint
    @RequestHandler("/")
    val rootHandler = HttpHandler { exchange ->
        if ((exchange!!.requestURI.toString() == "/") && (exchange.requestMethod.equals("GET"))) {
            val hardInfo = StringBuffer()
            hardInfo.append("Модель: " + Build.MODEL + "</br>")
            hardInfo.append("Устройство: " + Build.DEVICE + "</br>")
            hardInfo.append("Производитель: " + Build.MANUFACTURER + "</br>")
            hardInfo.append("Плата: " + Build.BOARD + "</br>")
            hardInfo.append("Марка: " + Build.BRAND + "</br>")
            hardInfo.append("Серийный номер: " + Build.SERIAL + "</br>")

            val softInfo = StringBuffer()
            try {
                val packageInfo: PackageInfo =
                    App.context.packageManager.getPackageInfo(App.context.packageName, 0)
                softInfo.append(
                    "Наименование: " + packageInfo.applicationInfo.loadLabel(App.context.packageManager)
                        .toString() + "</br>"
                )
                softInfo.append("Имя пакета: " + packageInfo.packageName + "</br>")
                softInfo.append("Версия: " + packageInfo.versionName + "</br>")
            } catch (e: Exception) {
                softInfo.append("Не удалось получить информацию")
            }

            val htmlResponse =
                "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /></head>" +
                        "<body><h2>Информация об устройстве:</h2>$hardInfo<h2>Информация о программе:</h2>" +
                        "$softInfo</body></html>"
            server.sendResponse(exchange, htmlResponse)
        } else server.sendResponse(exchange, makeNotImplementedError())
    }


    // info endpoint
    class AppInfo {
        var version_code: Int = BuildConfig.VERSION_CODE
        var version_name: String = BuildConfig.VERSION_NAME
        var application_id: String = BuildConfig.APPLICATION_ID
    }

    @RequestHandler("/info")
    val infoHandler = HttpHandler { exchange ->
        if (exchange!!.requestMethod.equals("GET")) {
            server.sendResponse(exchange, AppInfo())
        } else server.sendResponse(exchange, makeNotImplementedError())
    }

    // documents endpoint
    @RequestHandler("/document")
    val documentHandler = HttpHandler { exchange ->
        val documentID = exchange.requestURI.getFileName()
        try {
            when (exchange!!.requestMethod) {
                "GET" -> {
                    if (documentID == "") {
                        val documents = DocumentModel.getAllHeaders()
                        server.sendResponse(exchange, documents)
                    } else {
                        val document =
                            DocumentModel.getDocumentRows(documentID)
                        server.sendResponse(exchange, document)
                    }
                }
                "DELETE" -> {
                    if (documentID == "") {
                        DocumentModel.deleteAllDocuments()
                    } else {
                        DocumentModel.deleteDocument(documentID)
                    }
                    server.sendResponse(exchange, makeOkError())
                }
                "POST" -> {
                    val json = String(exchange.requestBody.readBytes(), Charsets.UTF_8)
                    val documentList: List<ViewDocument>
                    val listType = object : TypeToken<List<ViewDocument>>() {}.type
                    try {
                        documentList = Gson().fromJson(json, listType)
                    } catch (e: Exception) {
                        server.sendResponse(exchange, makeBadRequestError())
                        return@HttpHandler
                    }
                    DocumentModel.saveDocuments(documentList)
                    server.sendResponse(exchange, makeOkError())
                }
                else -> server.sendResponse(
                    exchange,
                    makeNotImplementedError()
                )
            }
        } catch (e: Exception) {
            server.sendResponse(
                exchange, makeServerError(e.message)
            )
            return@HttpHandler
        }

    }

    // good endpoint
    @RequestHandler("/good")
    val goodHandler = HttpHandler { exchange ->
        val goodID = exchange.requestURI.getFileName()
        try {
            when (exchange!!.requestMethod) {
                "GET" -> {
                    if (goodID == "") {
                        val goods = GoodModel.getAllViewGoods()
                        server.sendResponse(exchange, goods)
                    } else {
                        val good = GoodModel.getViewGood(goodID)
                        if (good != null)
                            server.sendResponse(exchange, good)
                        else
                            server.sendResponse(
                                exchange,
                                makeNotFoundError()
                            )
                    }
                }
                "DELETE" -> {
                    try {
                        if (goodID == "") {
                            GoodModel.deleteAllGoods()
                        } else {
                            GoodModel.deleteGood(goodID)
                        }
                        server.sendResponse(exchange, makeOkError())
                    } catch (e: SQLiteConstraintException) {
                        server.sendResponse(
                            exchange,
                            makeServerError(App.context.getString(R.string.error_delete_good))
                        )
                    }
                }
                "POST" -> {
                    val listType = object : TypeToken<List<ViewGood>>() {}.type
                    val json = String(exchange.requestBody.readBytes(), Charsets.UTF_8)
                    val goodList: List<ViewGood>
                    try {
                        goodList = Gson().fromJson(json, listType)
                    } catch (e: Exception) {
                        server.sendResponse(exchange, makeBadRequestError())
                        return@HttpHandler
                    }

                    GoodModel.saveViewGoods(goodList)
                    server.sendResponse(exchange, makeOkError())
                }
                else -> server.sendResponse(
                    exchange,
                    makeNotImplementedError()
                )
            }
        } catch (e: Exception) {
            server.sendResponse(
                exchange, makeServerError(e.message)
            )
            return@HttpHandler
        }
    }
}