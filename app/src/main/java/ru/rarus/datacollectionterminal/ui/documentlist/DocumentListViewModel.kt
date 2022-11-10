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
            if (document != null) {
                document.saved = true
            }
            data.postValue(document)
        }
        return data
    }
}