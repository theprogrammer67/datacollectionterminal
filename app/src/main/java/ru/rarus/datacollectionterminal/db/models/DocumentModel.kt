package ru.rarus.datacollectionterminal.db.models

import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.ViewDocument


class DocumentModel {

    fun getDocument(id: String): ViewDocument? {
        val header = App.database.getDao().getDocumentSync(id)
        return if (header != null) {
            val rows = App.database.getDao().getViewDocumentRowsSync(id)
            ViewDocument(header, rows, true)
        } else null
    }

    fun saveDocument(document: ViewDocument) {
        val dao = App.database.getDao()
        App.database.runInTransaction {
            dao.deleteDocumentRowsSync(document.document.id)
            dao.upsertDocumentSync(document.document)
            dao.insertDocumentRowsSync(document.rows)
            document.saved = true
        }
    }
}