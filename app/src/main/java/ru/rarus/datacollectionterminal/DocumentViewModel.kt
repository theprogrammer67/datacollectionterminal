package ru.rarus.datacollectionterminal

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.zxing.integration.android.IntentIntegrator

class DocumentViewModel : ViewModel() {
    var activity: Activity? = null
    val REQUEST_CODE = 1

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