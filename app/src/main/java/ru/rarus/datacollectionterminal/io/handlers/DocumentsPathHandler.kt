package ru.rarus.datacollectionterminal.io.handlers

import com.sun.net.httpserver.HttpExchange
import ru.rarus.datacollectionterminal.io.RestServer


const val DOCUMENT_PATH = "/document"

class DocumentsPathHandler(server: RestServer) : BasePathHandler(server, DOCUMENT_PATH) {

    override fun onMethGet(exchange: HttpExchange, id: String): Any? {
        return null
    }
}