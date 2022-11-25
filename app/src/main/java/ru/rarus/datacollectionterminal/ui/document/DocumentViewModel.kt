package ru.rarus.datacollectionterminal.ui.document

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.entities.DocumentRow
import ru.rarus.datacollectionterminal.db.entities.GoodAndUnit
import ru.rarus.datacollectionterminal.db.entities.ViewDocument
import ru.rarus.datacollectionterminal.db.models.DocumentModel
import ru.rarus.datacollectionterminal.db.models.GoodModel
import ru.rarus.datacollectionterminal.notifyObserver
import java.util.ArrayList

class DocumentViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var document = MutableLiveData<ViewDocument>()
    val selectedItems: ArrayList<String> = ArrayList()

    init {
        document.value = ViewDocument()
    }

    fun getData(id: String) : Flow<ViewDocument?> {
        return flow {
            emit(DocumentModel.getDocument(id))
        }
    }


    fun onScanBarcode(barcode: String, addBarcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val goodAndUnit = GoodModel.getGoodAndUnit(barcode)
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

        val goodAndUnit = GoodAndUnit(barcodeData)
        viewModelScope.launch(Dispatchers.IO) {
            App.database.getDao().insertGoodAndUnit(goodAndUnit)
            withContext(Dispatchers.Main) { addDocumentRow(goodAndUnit, addBarcode) }
        }
    }

    private fun addDocumentRow(goodAndUnit: GoodAndUnit, addBarcode: String) {
        document.value?.addRow(goodAndUnit, addBarcode)
        document.value!!.saved = false
        document.notifyObserver()
    }

    fun deleteSelectedRows() {
        if (document.value != null) {
            document.value!!.rows.filter { selectedItems.contains(it.id) }
                .forEach { document.value!!.rows.remove(it) }
            selectedItems.clear()
            document.value!!.saved = false
            document.notifyObserver()
        }
    }

    fun clearRows() {
        if (document.value != null) {
            document.value!!.rows.clear()
            document.value!!.saved = false
            document.notifyObserver()
        }
    }

    fun incRowQuantity (row: DocumentRow, value: Int) {
        if (document.value != null) {
            val quantityActual = row.quantityActual
            document.value!!.saved = false
            row.quantityActual = quantityActual + value
            document.notifyObserver()
        }
    }

    fun saveDocument() {
        if (document.value != null) {
            viewModelScope.launch(Dispatchers.IO) {
                DocumentModel.saveDocument(document.value!!)
                withContext(Dispatchers.Main) {
                    App.showMessage("Документ сохранен")
                }
            }
        }
    }
}