package ru.rarus.datacollectionterminal.ui.documentlist

import android.annotation.SuppressLint
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.entities.DocumentHeader
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

    fun deleteSelectedItems() {
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