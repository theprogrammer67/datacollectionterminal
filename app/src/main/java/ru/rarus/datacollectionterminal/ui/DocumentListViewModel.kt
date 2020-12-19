package ru.rarus.datacollectionterminal.ui

import androidx.lifecycle.ViewModel
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DctDocumentHeader

class DocumentListViewModel() : ViewModel() {
    var activity: DocumentListActivity? = null
    var documents: List<DctDocumentHeader> = ArrayList()

    fun getData() {
        if (activity == null) return

        val liveData = App.database.dctDao().getDocuments()
        liveData.observe(activity!!, {
            documents = it ?: ArrayList()
            activity!!.setDocumentList(documents)
        })
    }
}