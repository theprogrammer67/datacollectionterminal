package ru.rarus.datacollectionterminal.io

import com.sun.net.httpserver.HttpExchange

open class BasePathHandler(val server: RestServer, val path: String) {

    open fun onMethGet(exchange: HttpExchange) {

    }

}