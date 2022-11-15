package ru.rarus.datacollectionterminal.ui.documentlist

import android.annotation.SuppressLint
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DocumentHeader
import ru.rarus.datacollectionterminal.db.ViewDocument
import java.util.ArrayList

class DocumentListViewModel() : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var documents = MutableLiveData<List<DocumentHeader>>()
    val selectedDocs: ArrayList<String> = ArrayList()

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

    fun deleteSelected() {
        if (selectedDocs.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                selectedDocs.forEach {
                    App.database.getDao().deleteDocumentSync(it)
                }
                documents.postValue(App.database.getDao().getDocumentsSync())
                selectedDocs.clear()
            }
        }
    }
}