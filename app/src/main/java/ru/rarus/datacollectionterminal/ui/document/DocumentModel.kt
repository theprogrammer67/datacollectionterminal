package ru.rarus.datacollectionterminal.ui.document

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.GoodAndUnit
import ru.rarus.datacollectionterminal.db.ViewDocument

class DocumentModel(documentViewModel: DocumentViewModel) {
    private val viewModel = documentViewModel

    fun getGoodAndUnitByBarcode(barcodeData: String): LiveData<GoodAndUnit> {
        return App.database.getDao().getGoodAndUnitByBarcode(barcodeData)
    }

    fun insertGoodAndUnit(goodAndUnit: GoodAndUnit, onInsert: (GoodAndUnit) -> Unit) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            App.database.getDao().insertGoodAndUnit(goodAndUnit)
            withContext(Dispatchers.Main) { onInsert(goodAndUnit) }
        }
    }

    fun saveDocument(document: ViewDocument, onSave: (document: ViewDocument) -> Unit) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            if (document.saved)
                App.database.getDao().updateViewDocumentSync(document)
            else
                App.database.getDao().insertViewDocument(document)

            withContext(Dispatchers.Main) { onSave(document) }
        }
    }
}
