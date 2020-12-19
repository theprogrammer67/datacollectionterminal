package ru.rarus.datacollectionterminal.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityDocumentBinding
import ru.rarus.datacollectionterminal.databinding.DocumentRowBinding
import ru.rarus.datacollectionterminal.db.DctDocumentRow


class DocumentActivity() : AppCompatActivity() {
    private lateinit var viewModel: DocumentViewModel
    private lateinit var binding: ActivityDocumentBinding
    private lateinit var adapter: DocumentRowsAdapter
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_document)

        viewModel = ViewModelProvider(this).get(DocumentViewModel::class.java)
        viewModel.activity = this

        adapter = DocumentRowsAdapter(viewModel.document.rows, applicationContext)
        binding.lvRows.adapter = adapter

        binding.btnScanBarcode.setOnClickListener { viewModel.scanBarcode() }
        binding.btnSaveDocument.setOnClickListener { viewModel.saveDocument() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun onDeleteClick(menuItem: MenuItem) {
        viewModel.deleteSelectedRows()
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
                App.showMessage("Сканирование отменено")
            else
                viewModel.onScanBarcode(result.contents)
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    fun onChangeDocument() {
        adapter.notifyDataSetChanged()
    }
}

class DocumentRowsAdapter(
    private val documentRows: List<DctDocumentRow>,
    private val context: Context
) : BaseAdapter() {

    override fun getCount(): Int = documentRows.size

    override fun getItem(position: Int): Any = documentRows[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DocumentRowBinding

        if (convertView == null) {
            binding = DocumentRowBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
            binding.chbSelected.setOnClickListener {
                (getItem(position) as DctDocumentRow).isSelected = (it as CheckBox).isChecked
            }
        } else
            binding = convertView.tag as DocumentRowBinding

        binding.dctDocumentRow = getItem(position) as DctDocumentRow

        return binding.root
    }
}