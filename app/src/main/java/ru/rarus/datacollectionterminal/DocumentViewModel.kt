package ru.rarus.datacollectionterminal

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.zxing.integration.android.IntentIntegrator
import java.util.*

class DocumentViewModel : ViewModel() {
    var activity: Activity? = null
    val REQUEST_CODE = 1
    val tmp = "temp"
    val docTable: MutableList<DctDocumentRow> = ArrayList()

    fun scanBarcode() {
        if (activity == null) return

        val integrator = IntentIntegrator(this.activity)
        integrator.captureActivity = ScannerCaptureActivity::class.java
        integrator.setRequestCode(REQUEST_CODE)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    fun onScanBarcode(barcodeData: String){
    }
}