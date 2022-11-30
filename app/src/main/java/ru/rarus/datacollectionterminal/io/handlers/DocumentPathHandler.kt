package ru.rarus.datacollectionterminal.io.handlers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.rarus.datacollectionterminal.db.entities.ViewDocument
import ru.rarus.datacollectionterminal.db.models.DocumentModel
import ru.rarus.datacollectionterminal.io.Errors
import ru.rarus.datacollectionterminal.io.HTTP_CODE_BAD_REQUEST
import ru.rarus.datacollectionterminal.io.BaseRestServer


const val DOCUMENT_PATH = "/document"

class DocumentPathHandler(server: BaseRestServer) : BasePathHandler(server, DOCUMENT_PATH) {

    override fun onMethGet(id: String): Any? {
        return if (id == "") {
            DocumentModel.getAllHeaders()
        } else {
            DocumentModel.getDocumentRows(id)
        }
    }

    override fun onMethDelete(id: String) {
        if (id == "") {
            DocumentModel.deleteAllDocuments()
        } else {
            DocumentModel.deleteDocument(id)
        }
    }

    override fun onMethPost(json: String, id: String) {
        val documentList: List<ViewDocument>
        val listType = object : TypeToken<List<ViewDocument>>() {}.type
        try {
            documentList = Gson().fromJson(json, listType)
        } catch (e: Exception) {
            throw Errors.createHttpException(HTTP_CODE_BAD_REQUEST)
        }
        DocumentModel.saveDocuments(documentList)
    }
}