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

    fun incRowQuantity (position: Int, value: Int) {
        if (document.value != null) {
            val quantityActual = document.value!!.rows[position].quantityActual
            document.value!!.saved = false
            document.value!!.rows[position].quantityActual = quantityActual + value
            document.notifyObserver()
        }
    }

    fun saveDocument() {
        if (document.value != null) {
            val data: ViewDocument = document.value!!

            viewModelScope.launch(Dispatchers.IO) {
                App.database.getDao().upsertViewDocumentSync(data)
                withContext(Dispatchers.Main) {
                    App.showMessage("Документ сохранен")
                }
            }
        }
    }
}