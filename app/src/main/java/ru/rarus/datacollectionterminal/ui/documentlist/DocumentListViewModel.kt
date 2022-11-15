package ru.rarus.datacollectionterminal.ui.documentlist

import android.annotation.SuppressLint
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DocumentHeader
import ru.rarus.datacollectionterminal.db.ViewDocument
import ru.rarus.datacollectionterminal.notifyObserver

class DocumentListViewModel() : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var documents = MutableLiveData<List<DocumentHeader>>()

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            documents.postValue(App.database.getDao().getDocumentsSync())
        }
    }

    fun getDocument(documentId: String): LiveData<ViewDocument?> {
        val data = MutableLiveData<ViewDocument?>()
        viewModelScope.launch(Dispatchers.IO) {
            val document = App.database.getDao().getViewDocument(documentId)
            data.postValue(document)
        }
        return data
    }

    fun deleteSelectedItems() {
        if (documents.value != null) {
            viewModelScope.launch(Dispatchers.IO) {
                documents.value!!.filter { it.isSelected }
                    .forEach {
                        App.database.getDao().deleteDocumentSync(it.id)
                    }
                documents.postValue(App.database.getDao().getDocumentsSync())
            }
        }
    }
}