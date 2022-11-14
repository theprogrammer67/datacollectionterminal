package ru.rarus.datacollectionterminal.ui.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityScannerBinding

const val cameraPermissionReqCode = 250

class ScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScannerBinding
    private lateinit var beepManager: BeepManager
    private var lastText: String = ""
    private lateinit var barcodeView: DecoratedBarcodeView
    private var askedPermission = false
    private var barcode = ""
    private var extBarcode = ""
    val extBarcodeRead = App.prefs.getBoolean("extBarcodeRead", false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_scanner
        )
        barcodeView = binding.barcodeScanner
        barcodeView.initializeFromIntent(intent)
        barcodeView.decodeContinuous(callback)

        beepManager = BeepManager(this)
    }

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) {
                // Prevent duplicate scans
                return
            }
            lastText = result.text
            barcodeView.setStatusText(result.text)
            beepManager.playBeepSoundAndVibrate()

            if (!extBarcodeRead) {
                barcode = result.text
                extBarcode = ""
                returnData()
            } else {
                if (barcode.isEmpty()) {
                    barcode = result.text
                    pause(null)
                } else {
                    extBarcode = result.text
                    returnData()
                }
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    fun pause(view: View?) {
        barcodeView.pause()
        binding.btnPause.isEnabled = false
        binding.btnResume.isEnabled = true
    }

    fun resume(view: View?) {
        barcodeView.resume()
        binding.btnPause.isEnabled = true
        binding.btnResume.isEnabled = false
    }

    fun cancel(view: View?) {
        val data = Intent()
        setResult(RESULT_CANCELED, data)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        openCameraWithPermission()
        resume(null)
    }

    private fun openCameraWithPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            barcodeView.resume()
        } else if (!askedPermission) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), cameraPermissionReqCode
            )
            askedPermission = true
        }
    }

    fun returnData() {
        val data = Intent()
        data.putExtra("barcode", barcode)
        data.putExtra("extBarcode", extBarcode)
        setResult(RESULT_OK, data)
        finish()
    }
}
