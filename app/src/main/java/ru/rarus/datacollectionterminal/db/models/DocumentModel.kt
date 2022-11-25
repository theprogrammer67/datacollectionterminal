package ru.rarus.datacollectionterminal.db.models

import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.entities.*


class DocumentModel {
    companion object {
        @JvmStatic
        fun getDocument(id: String): ViewDocument? {
            val dao = App.database.getDocumentDao()
            val header = dao.getDocument(id)
            return if (header != null) {
                val rows = dao.getViewDocumentRows(id)
                ViewDocument(header, rows, true)
            } else null
        }

        @JvmStatic
        fun getDocumentRows(id: String): List<ViewDocumentRow> {
            return App.database.getDocumentDao().getViewDocumentRows(id)
        }

        @JvmStatic
        fun saveDocument(document: ViewDocument) {
            val dao = App.database.getDocumentDao()
            App.database.runInTransaction {
                dao.deleteDocument(document.document.id)
                dao.insertDocument(document.document)
                dao.insertDocumentRows(document.rows)
                document.saved = true
            }
        }

        @JvmStatic
        fun saveDocuments(items: List<ViewDocument>) {
            val dao = App.database.getDocumentDao()
            val ids = mutableListOf<String>()
            val headers = mutableListOf<DocumentHeader>()
            val rows = mutableListOf<DocumentRow>()
            items.forEach {
                ids.add(it.document.id)
                headers.add(it.document)
                rows.addAll(it.rows)
            }
            App.database.runInTransaction {
                dao.deleteDocuments(ids)
                dao.insertDocuments(headers)
                dao.insertDocumentRows(rows)
            }
            items.forEach { it.saved = true }
        }

        @JvmStatic
        fun deleteDocument(id: String) {
            App.database.getDocumentDao().deleteDocument(id)
        }

        @JvmStatic
        fun deleteAllDocuments() {
            App.database.getDocumentDao().deleteAllDocuments()
        }

        @JvmStatic
        fun getAllHeaders(): List<DocumentHeader> {
            return App.database.getDocumentDao().getAllDocuments()
        }
    }
}