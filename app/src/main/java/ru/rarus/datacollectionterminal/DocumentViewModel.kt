package ru.rarus.datacollectionterminal

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DocumentViewModel : ViewModel() {
    var activity: DocumentActivity? = null
    val document = DctFocument()

    fun scanBarcode() {
        // По-умолчанию используем zxing сканер
        // Но тут могут быть и другие варианты (bluetooth-сканер)
        activity?.startScanActivity()
    }

    fun onScanBarcode(barcodeData: String) {
        if (activity == null) return

        val dao = App.database.dctDao()
        val goodAndUnit = dao.getGoodAndUnitByBarcode(barcodeData)
        goodAndUnit.observeOnce(activity!!, {
            if (it == null) {
                onBarcodeNotFound(barcodeData)
            } else {
                addDocumentRow(it)
            }
        })
    }

    private fun addDocumentRow(goodAndUnit: GoodAndUnit) {
        document.rows.add(ViewDocumentRow(goodAndUnit))
        activity!!.refreshList()
    }

    private fun onBarcodeNotFound(barcodeData: String) {
        App.showMessage("Штрихкод не найден")
        val goodAndUnit = GoodAndUnit(barcodeData)
        GlobalScope.launch {
            App.database.dctDao().insertGoodAndUnits(goodAndUnit)
            withContext(Dispatchers.Main) { addDocumentRow(goodAndUnit) }
        }
    }
}