package ru.rarus.datacollectionterminal.ui.documentlist

import androidx.lifecycle.ViewModel
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DocumentHeader

class DocumentListViewModel() : ViewModel() {
    var activity: DocumentListActivity? = null
    var documents: List<DocumentHeader> = ArrayList()

    fun getData() {
        if (activity == null) return

        val liveData = App.database.getDao().getDocuments()
        liveData.observe(activity!!, {
            documents = it ?: ArrayList()
            activity!!.setDocumentList(documents)
        })
    }
}