package ru.rarus.datacollectionterminal.ui.documentlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DocumentHeader

class DocumentListViewModel() : ViewModel() {
    var activity: DocumentListActivity? = null
    var documents = MutableLiveData<List<DocumentHeader>>()

    fun getData() {
        if (activity == null) return
        App.database.getDao().getDocuments()
            .observe(activity!!, { documents.value = it ?: ArrayList() })
    }
}