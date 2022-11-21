package ru.rarus.datacollectionterminal.ui.document

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
}