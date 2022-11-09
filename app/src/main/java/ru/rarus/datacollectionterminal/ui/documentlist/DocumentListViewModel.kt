package ru.rarus.datacollectionterminal.ui.documentlist

import android.annotation.SuppressLint
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DocumentHeader
import ru.rarus.datacollectionterminal.db.ViewDocument

class DocumentListViewModel() : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var activity: DocumentListActivity? = null
    var documents = MutableLiveData<List<DocumentHeader>>()

    fun getData() {
        if (activity == null) return

        val data = MutableLiveData<List<DocumentHeader>>()
        viewModelScope.launch(Dispatchers.IO) {
            data.postValue(App.database.getDao().getDocumentsSync())
        }
        activity?.let { data.observe(it, { documents.setValue(it) }) }
    }

    fun getDocument(documentId: String): LiveData<ViewDocument?> {
        val data = MutableLiveData<ViewDocument?>()
        viewModelScope.launch(Dispatchers.IO) {
            val document = App.database.getDao().getViewDocument(documentId)
            if (document != null) {
                document.saved = true
            }
            data.postValue(document)
        }
        return data
    }
}