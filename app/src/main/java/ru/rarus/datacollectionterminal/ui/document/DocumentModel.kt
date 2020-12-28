package ru.rarus.datacollectionterminal.ui.document

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.GoodAndUnit
import ru.rarus.datacollectionterminal.db.ViewDocument

class DocumentModel {

    fun getGoodAndUnitByBarcode(barcodeData: String): LiveData<GoodAndUnit> {
        return App.database.getDao().getGoodAndUnitByBarcode(barcodeData)
    }

    fun insertGoodAndUnit(goodAndUnit: GoodAndUnit, onInsert: (GoodAndUnit) -> Unit) {
        GlobalScope.launch {
            App.database.getDao().insertGoodAndUnit(goodAndUnit)
            withContext(Dispatchers.Main) { onInsert(goodAndUnit) }
        }
    }

    fun saveDocument(document: ViewDocument, onSave: (document: ViewDocument) -> Unit) {
        GlobalScope.launch {
            if (document.saved)
                App.database.getDao().updateViewDocument(document)
            else
                App.database.getDao().insertViewDocument(document)

            withContext(Dispatchers.Main) { onSave(document) }
        }
    }

    fun getData(documentId: String): LiveData<ViewDocument> {
        val data = MutableLiveData<ViewDocument>()
        GlobalScope.launch {
            val document = App.database.getDao().getViewDocument(documentId)
            data.postValue(document)
        }
        return data
    }
}
