package ru.rarus.datacollectionterminal.ui.documentlist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DocumentHeader

class DocumentListViewModel() : ViewModel() {
    var documents = MutableLiveData<List<DocumentHeader>>()

    fun getData(owner: LifecycleOwner) {
        val data = MutableLiveData<List<DocumentHeader>>()
        GlobalScope.launch(Dispatchers.IO) {
            data.postValue(App.database.getDao().getDocumentsSync())
        }
        data.observe(owner, { documents.setValue(it) })
    }
}