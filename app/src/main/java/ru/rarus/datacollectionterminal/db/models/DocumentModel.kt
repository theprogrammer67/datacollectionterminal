package ru.rarus.datacollectionterminal.db.models

import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.entities.DocumentHeader
import ru.rarus.datacollectionterminal.db.entities.ViewDocument
import ru.rarus.datacollectionterminal.db.entities.ViewDocumentRow


class DocumentModel {
    companion object {
        @JvmStatic
        fun getDocument(id: String): ViewDocument? {
            val dao = App.database.getDao()
            val header = dao.getDocumentSync(id)
            return if (header != null) {
                val rows = dao.getViewDocumentRowsSync(id)
                ViewDocument(header, rows, true)
            } else null
        }

        @JvmStatic
        fun getDocumentRows(id: String): List<ViewDocumentRow> {
            return App.database.getDao().getViewDocumentRowsSync(id)
        }

        @JvmStatic
        fun saveDocument(document: ViewDocument) {
            val dao = App.database.getDao()
            App.database.runInTransaction {
                dao.deleteDocumentRowsSync(document.document.id)
                dao.upsertDocumentSync(document.document)
                dao.insertDocumentRowsSync(document.rows)
                document.saved = true
            }
        }

        @JvmStatic
        fun saveDocuments(documents: List<ViewDocument>) {
            documents.forEach() {
                saveDocument(it)
            }
        }

        @JvmStatic
        fun deleteDocument(id: String) {
            App.database.getDao().deleteDocumentSync(id)
        }

        @JvmStatic
        fun deleteAllDocuments() {
            App.database.getDao().deleteDocumentsSync()
        }

        @JvmStatic
        fun getAllHeaders(): List<DocumentHeader> {
            return App.database.getDao().getDocumentsSync()
        }
    }
}