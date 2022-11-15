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
    var list = MutableLiveData<List<DocumentHeader>>()
    val selectedItems: ArrayList<String> = ArrayList()

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            list.postValue(App.database.getDao().getDocumentsSync())
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
        if (selectedItems.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                selectedItems.forEach {
                    App.database.getDao().deleteDocumentSync(it)
                }
                list.postValue(App.database.getDao().getDocumentsSync())
                selectedItems.clear()
            }
        }
    }
}