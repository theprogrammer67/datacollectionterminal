package ru.rarus.datacollectionterminal.ui.document

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.GoodAndUnit
import ru.rarus.datacollectionterminal.db.ViewDocument
import ru.rarus.datacollectionterminal.notifyObserver
import ru.rarus.datacollectionterminal.observeOnce

class DocumentViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var document = MutableLiveData<ViewDocument>()
    var model = DocumentModel(this)

    init {
        document.value = ViewDocument()
    }

    fun onScanBarcode(barcodeData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val goodAndUnit = model.getGoodAndUnitByBarcode(barcodeData)
            withContext(Dispatchers.Main) {
                if (goodAndUnit == null)
                    onBarcodeNotFound(barcodeData)
                else
                    addDocumentRow(goodAndUnit)
            }

        }
    }

    private fun addDocumentRow(goodAndUnit: GoodAndUnit) {
        document.value?.addRow(goodAndUnit)
        document.notifyObserver()
    }

    private fun onBarcodeNotFound(barcodeData: String) {
        // Здесь в зависимости от настроек или добавляем или ругаемся
        App.showMessage("Штрихкод не найден")
        model.insertGoodAndUnit(GoodAndUnit(barcodeData)) { addDocumentRow(it) }
    }

    fun deleteSelectedRows() {
        if (document.value != null) {
            document.value!!.rows.filter { it.isSelected }
                .forEach { document.value!!.rows.remove(it) }
            document.notifyObserver()
        }
    }

    fun saveDocument() {
        if (document.value != null)
            model.saveDocument(document.value!!) {
                it.saved = true
                App.showMessage("Документ сохранен")
            }
    }
}