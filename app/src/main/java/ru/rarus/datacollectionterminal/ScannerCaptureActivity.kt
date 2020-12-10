package ru.rarus.datacollectionterminal

import android.view.View
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView


class ScannerCaptureActivity: CaptureActivity() {

    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_scanner_capture)
        return findViewById<View>(R.id.zxing_barcode_scanner) as DecoratedBarcodeView
    }

}
