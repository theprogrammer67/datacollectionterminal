package ru.rarus.datacollectionterminal

import androidx.lifecycle.ViewModel

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

        document.rows.add(DctDocumentRow(barcodeData))
        activity!!.refreshList()

        val unit = App.database.dctDao().getUnitByBarcode(barcodeData)
        unit.observe(activity!!, {
            if (it == null)
                App.showMessage("Штрихкод не найден")
        })
    }
}