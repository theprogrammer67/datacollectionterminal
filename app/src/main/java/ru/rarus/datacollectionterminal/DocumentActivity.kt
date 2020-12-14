package ru.rarus.datacollectionterminal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import ru.rarus.datacollectionterminal.databinding.ActivityDocumentBinding
import ru.rarus.datacollectionterminal.databinding.DocumentRowBinding

class DocumentActivity : AppCompatActivity() {
    private lateinit var viewModel: DocumentViewModel
    private lateinit var binding: ActivityDocumentBinding
    private lateinit var adapter: DocumentRowsAdapter
    val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_document)

        viewModel = ViewModelProvider(this).get(DocumentViewModel::class.java)
        viewModel.activity = this

        adapter = DocumentRowsAdapter(viewModel.document.rows, applicationContext)
        binding.rvRows.adapter = adapter

        binding.btnScanBarcode.setOnClickListener {
            viewModel.scanBarcode()
        }
    }

    fun startScanActivity() {
        val integrator = IntentIntegrator(this)
        integrator.captureActivity = ScannerCaptureActivity::class.java
        integrator.setRequestCode(REQUEST_CODE)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null) {
            if (result.contents == null)
                App.showMessage("Скнирование отменено")
            else
                viewModel.onScanBarcode(result.contents)
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    fun refreshList() {
        adapter.notifyDataSetChanged()
    }
}

class DocumentRowsAdapter(
    private val documentRows: List<ViewDocumentRow>,
    private val context: Context
) : BaseAdapter() {

    override fun getCount(): Int = documentRows.size

    override fun getItem(position: Int): Any = documentRows[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DocumentRowBinding

        if (convertView == null) {
            binding = DocumentRowBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        } else
            binding = convertView.tag as DocumentRowBinding

        binding.viewDocumentRow = getItem(position) as ViewDocumentRow

        return binding.root
    }
}