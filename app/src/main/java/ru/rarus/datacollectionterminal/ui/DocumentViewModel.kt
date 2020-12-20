package ru.rarus.datacollectionterminal.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.DocumentAndRows
import ru.rarus.datacollectionterminal.db.GoodAndUnit
import ru.rarus.datacollectionterminal.observeOnce

class DocumentViewModel : ViewModel() {
    var activity: DocumentActivity? = null
    var document = DocumentAndRows()

    fun scanBarcode() {
        // По-умолчанию используем zxing сканер
        // Но тут могут быть и другие варианты (bluetooth-сканер)
        activity?.startScanActivity()
    }

    fun onScanBarcode(barcodeData: String) {
        if (activity == null) return

        val dao = App.database.getDao()
        val goodAndUnit = dao.getGoodAndUnitByBarcode(barcodeData)
        goodAndUnit.observeOnce(activity!!, {
            if (it == null)
                onBarcodeNotFound(barcodeData)
            else
                addDocumentRow(it)
        })
    }

    private fun addDocumentRow(goodAndUnit: GoodAndUnit) {
        document.addRow(goodAndUnit)
        activity!!.onChangeDocument()
    }

    private fun onBarcodeNotFound(barcodeData: String) {
        // Здесь в зависимости от настроек или добавляем или ругаемся
        App.showMessage("Штрихкод не найден")
        val goodAndUnit = GoodAndUnit(barcodeData)
        GlobalScope.launch {
            App.database.getDao().insertGoodAndUnit(goodAndUnit)
            withContext(Dispatchers.Main) { addDocumentRow(goodAndUnit) }
        }
    }

    fun deleteSelectedRows() {
        document.rows.filter { it.isSelected }.forEach { document.rows.remove(it) }
        activity!!.onChangeDocument()
    }

    fun saveDocument() {
        GlobalScope.launch {
            if (document.saved)
                App.database.getDao().updateDocumentAndRows(document)
            else
                App.database.getDao().insertDocumentAndRows(document)

            document.saved = true
            withContext(Dispatchers.Main) { App.showMessage("Документ сохранен") }
        }
    }

    fun getData(documentId: String?) {
        if (documentId == null) return

        val liveData = App.database.getDao().getDocumentAndRows(documentId)
        liveData.observe(activity!!, {
            it?.saved = true
            document = it ?: DocumentAndRows()
            activity!!.setDocument(document)
        })
    }
}