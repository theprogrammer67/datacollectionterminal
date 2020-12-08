package ru.rarus.datacollectionterminal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import ru.rarus.datacollectionterminal.databinding.ActivityDocumentBinding

class DocumentActivity : AppCompatActivity() {
    private lateinit var viewModel: DocumentViewModel
    private lateinit var binding: ActivityDocumentBinding
    val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(DocumentViewModel::class.java)

        binding.btnScanBarcode.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setRequestCode(REQUEST_CODE)
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null) {
            if (result.contents == null)
                Toast.makeText(this, "Скнирование отменено", Toast.LENGTH_LONG).show()
            else {
                viewModel.onScanBarcode(result.contents)
                Toast.makeText(this, "Штрихкод: ${result.contents}", Toast.LENGTH_LONG).show()
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

}