package ru.rarus.datacollectionterminal.io

import android.content.pm.PackageInfo
import android.os.Build
import com.google.gson.Gson
import com.sun.net.httpserver.HttpHandler
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DocumentHeader
import ru.rarus.datacollectionterminal.db.ViewDocument
import ru.rarus.datacollectionterminal.db.ViewDocumentRow
import ru.rarus.datacollectionterminal.db.ViewGood


class Handlers(private val server: RestServer) {

    annotation class RequestHandler(val path: String)

    data class HandlerError(val code: Int, val message: String)

    private fun makeNotImplementedError(): HandlerError {
        return HandlerError(406, "Метод не поддерживается")
    }

    private fun makeNotFoundError(): HandlerError {
        return HandlerError(404, "Ресурс не найден")
    }

    private fun makeBadRequestError(): HandlerError {
        return HandlerError(400, "Некорректный запрос")
    }

    // root endpoint
    @RequestHandler("/")
    val rootHandler = HttpHandler { exchange ->
        when (exchange!!.requestMethod) {
            "GET" -> {
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
            }
            else -> server.sendResponse(exchange, makeNotImplementedError())
        }
    }

    // documents endpoint
    @RequestHandler("/document")
    val documentHandler = HttpHandler { exchange ->
        val documentID = exchange.requestURI.getFileName()
        when (exchange!!.requestMethod) {
            "GET" -> {
                if (documentID == "") {
                    val documents = App.database.getDao().getDocumentsSync()
                    server.sendResponse<List<DocumentHeader>>(exchange, documents)
                } else {
                    val document = App.database.getDao().getViewDocumentRowsSync(documentID)
                    if (document.isNotEmpty())
                        server.sendResponse<List<ViewDocumentRow>>(exchange, document)
                    else
                        server.sendResponse<HandlerError>(exchange, makeNotFoundError())
                }
            }
            "DELETE" -> {
                if (documentID == "") {
                    App.database.getDao().deleteDocumentsSync()
                } else {
                    App.database.getDao().deleteDocumentSync(documentID)
                }
            }
            "POST" -> {
                val document =
                    Gson().fromJson(exchange.requestBody.toString(), ViewDocument::class.java)
                App.database.getDao().updateViewDocumentSync(document)
            }
            else -> server.sendResponse<HandlerError>(exchange, makeNotImplementedError())
        }
    }

    // good endpoint
    @RequestHandler("/good")
    val goodHandler = HttpHandler { exchange ->
        val goodID = exchange.requestURI.getFileName()
        when (exchange!!.requestMethod) {
            "GET" -> {
                if (goodID == "") {
                    val goods = App.database.getDao().getViewGoodsSync()
                    server.sendResponse<List<ViewGood>>(exchange, goods)
                } else {
                    val good = App.database.getDao().getViewGoodSync(goodID)
                    if (good != null)
                        server.sendResponse<ViewGood>(exchange, good)
                    else
                        server.sendResponse<HandlerError>(exchange, makeNotFoundError())
                }
            }
            "DELETE" -> {
                if (goodID == "") {
                    App.database.getDao().deleteGoodsSync()
                } else {
                    App.database.getDao().deleteGoodSync(goodID)
                }
            }
            "POST" -> {
                val good =
                    Gson().fromJson(exchange.requestBody.toString(), ViewGood::class.java)
                App.database.getDao().updateViewGoodSync(good)
            }
            else -> server.sendResponse<HandlerError>(exchange, makeNotImplementedError())
        }
    }
}