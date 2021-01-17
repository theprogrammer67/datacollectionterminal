package ru.rarus.datacollectionterminal.ui.documentlist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DocumentHeader

class DocumentListViewModel() : ViewModel() {
    var documents = MutableLiveData<List<DocumentHeader>>()

    fun getData(owner: LifecycleOwner) {
        App.database.getDao().getDocuments()
            .observe(owner, { documents.value = it ?: ArrayList() })
    }
}