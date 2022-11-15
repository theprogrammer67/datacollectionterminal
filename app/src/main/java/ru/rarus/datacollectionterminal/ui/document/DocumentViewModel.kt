package ru.rarus.datacollectionterminal.ui.document

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.GoodAndUnit
import ru.rarus.datacollectionterminal.db.ViewDocument
import ru.rarus.datacollectionterminal.notifyObserver
import java.util.ArrayList

class DocumentViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var document = MutableLiveData<ViewDocument>()
    val selectedItems: ArrayList<String> = ArrayList()

    init {
        document.value = ViewDocument()
    }

    fun onScanBarcode(barcode: String, addBarcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val goodAndUnit = App.database.getDao().getGoodAndUnitByBarcodeSync(barcode)
            withContext(Dispatchers.Main) {
                if (goodAndUnit == null)
                    onBarcodeNotFound(barcode, addBarcode)
                else
                    onBarcodeFound(goodAndUnit, addBarcode)
            }

        }
    }

    private fun onBarcodeFound(goodAndUnit: GoodAndUnit, addBarcode: String) {
        addDocumentRow(goodAndUnit, addBarcode)
    }

    private fun onBarcodeNotFound(barcodeData: String, addBarcode: String) {
        // Здесь в зависимости от настроек или добавляем или ругаемся
        App.showMessage("Штрихкод не найден")

        viewModelScope.launch(Dispatchers.IO) {
            val goodAndUnit = GoodAndUnit(barcodeData)
            App.database.getDao().insertGoodAndUnit(goodAndUnit)
            withContext(Dispatchers.Main) { addDocumentRow(goodAndUnit, addBarcode) }
        }
    }

    private fun addDocumentRow(goodAndUnit: GoodAndUnit, addBarcode: String) {
        document.value?.addRow(goodAndUnit, addBarcode)
        document.notifyObserver()
    }

    fun deleteSelectedRows() {
        if (document.value != null) {
            document.value!!.rows.filter { selectedItems.contains(it.id) }
                .forEach { document.value!!.rows.remove(it) }
            selectedItems.clear()
            document.notifyObserver()
        }
    }

    fun clewrRows() {
        if (document.value != null) {
            document.value!!.rows.clear()
            document.notifyObserver()
        }
    }

    fun saveDocument() {
        if (document.value != null) {
            val data: ViewDocument = document.value!!

            viewModelScope.launch(Dispatchers.IO) {
                if (data.saved)
                    App.database.getDao().updateViewDocumentSync(data)
                else
                    App.database.getDao().insertViewDocument(data)

                withContext(Dispatchers.Main) {
                    App.showMessage("Документ сохранен")
                }
            }
        }
    }
}