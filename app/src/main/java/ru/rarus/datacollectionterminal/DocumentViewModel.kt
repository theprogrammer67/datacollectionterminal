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

    fun onScanBarcode(barcodeData: String){
        document.rows.add(DctDocumentRow(barcodeData))
        activity?.refreshList()
    }
}