package ru.rarus.datacollectionterminal.ui.document

import androidx.lifecycle.ViewModel
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.GoodAndUnit
import ru.rarus.datacollectionterminal.db.ViewDocument
import ru.rarus.datacollectionterminal.observeOnce

class DocumentViewModel : ViewModel() {
    var activity: DocumentActivity? = null
    var document = ViewDocument()
    var model = DocumentModel()

    fun scanBarcode() {
        // По-умолчанию используем zxing сканер
        // Но тут могут быть и другие варианты (bluetooth-сканер)
        activity?.startScanActivity()
    }

    fun onScanBarcode(barcodeData: String) {
        if (activity == null) return

        model.getGoodAndUnitByBarcode(barcodeData).observeOnce(activity!!, {
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
        model.insertGoodAndUnit(GoodAndUnit(barcodeData)) { addDocumentRow(it) }
    }

    fun deleteSelectedRows() {
        document.rows.filter { it.isSelected }.forEach { document.rows.remove(it) }
        activity!!.onChangeDocument()
    }

    fun saveDocument() {
        model.saveDocument(document) {
            it.saved = true
            App.showMessage("Документ сохранен")
        }
    }

    fun getData(documentId: String?) {
        if (documentId == null) return

        model.getData(documentId).observeOnce(activity!!, {
            if (it != null) {
                document = it
                document.saved = true
                activity!!.setDocument(document)
            }
        })
    }
}