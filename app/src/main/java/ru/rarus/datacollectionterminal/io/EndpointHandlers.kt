package ru.rarus.datacollectionterminal.io

import android.content.pm.PackageInfo
import android.os.Build
import com.sun.net.httpserver.HttpHandler
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.TestData

class Handlers(private val server: RestServer) {

    annotation class RequestHandler(val path: String)

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
                hardInfo.append("Серийный номер: " + Build.SERIAL + "</br>");

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