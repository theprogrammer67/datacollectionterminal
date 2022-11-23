package ru.rarus.datacollectionterminal.ui.documentlist

import android.annotation.SuppressLint
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.entities.DocumentHeader
import ru.rarus.datacollectionterminal.db.models.DocumentModel
import java.util.ArrayList

class DocumentListViewModel : ViewModel() {
    var list = MutableLiveData<List<DocumentHeader>>()
    val selectedItems: ArrayList<String> = ArrayList()

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            list.postValue(DocumentModel.getAllHeaders())
        }
    }

    fun deleteSelectedItems() {
        if (selectedItems.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                selectedItems.forEach {
                    DocumentModel.deleteDocument(it)
                }
                selectedItems.clear()
                list.postValue(DocumentModel.getAllHeaders())
            }
        }
    }
}