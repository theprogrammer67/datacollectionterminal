package ru.rarus.datacollectionterminal.io

import com.sun.net.httpserver.HttpExchange

class DocumentsPathHandler(server: RestServer) : BasePathHandler(server,"/document") {

    override fun onMethGet(exchange: HttpExchange) {

    }
}