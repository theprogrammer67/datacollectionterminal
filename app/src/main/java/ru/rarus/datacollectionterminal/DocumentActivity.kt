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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(DocumentViewModel::class.java)
        viewModel.activity = this

        binding.btnScanBarcode.setOnClickListener {
            viewModel.scanBarcode()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != viewModel.REQUEST_CODE) {
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