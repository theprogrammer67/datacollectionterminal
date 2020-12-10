package ru.rarus.datacollectionterminal

import android.os.Bundle
import com.journeyapps.barcodescanner.CaptureActivity


class ScannerCaptureActivity: CaptureActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_scanner_capture)
    }

}
