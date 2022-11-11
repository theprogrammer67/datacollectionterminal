package ru.rarus.datacollectionterminal.ui

import android.view.View
import android.widget.TextView
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import ru.rarus.datacollectionterminal.R


class BarcodeCaptureActivity: CaptureActivity() {

    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_scanner_capture)
        val propmt = findViewById<TextView>(R.id.prompt)
        propmt.text = "Сканируйте штрихкод товара"
        return findViewById<View>(R.id.zxing_barcode_scanner) as DecoratedBarcodeView
    }

}
