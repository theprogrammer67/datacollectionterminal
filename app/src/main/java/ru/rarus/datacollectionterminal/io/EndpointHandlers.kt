package ru.rarus.datacollectionterminal.io

import android.content.pm.PackageInfo
import android.os.Build
import com.sun.net.httpserver.HttpHandler
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DocumentHeader
import ru.rarus.datacollectionterminal.db.ViewDocumentRow


class Handlers(private val server: RestServer) {

    annotation class RequestHandler(val path: String)

    data class HandlerError(val code: Int, val message: String)

    private fun makeNotImplementedError(): HandlerError {
        return HandlerError(406, "Метод не поддерживается")
    }

    private fun makeNotFoundError(): HandlerError {
        return HandlerError(404, "Ресурс не найден")
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
            else -> server.sendResponse<HandlerError>(exchange, makeNotImplementedError())
        }
    }
}