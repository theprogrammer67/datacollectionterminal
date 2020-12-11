package ru.rarus.datacollectionterminal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import ru.rarus.datacollectionterminal.databinding.ActivityDocumentBinding
import ru.rarus.datacollectionterminal.databinding.DocumentRowBinding

class DocumentActivity : AppCompatActivity() {
    private lateinit var viewModel: DocumentViewModel
    private lateinit var binding: ActivityDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_document)

        val listAdapter = DocomentRowsAdapter(applicationContext)
        val list = mutableListOf<DctDocumentRow>()
        for(n in 1..9) {
            list.add(DctDocumentRow(n.toString()))
        }
        listAdapter.documentRows = list
        binding.rvRows.adapter = listAdapter

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

class DocomentRowsAdapter(private val context: Context) : BaseAdapter() {
    var documentRows: List<DctDocumentRow> = emptyList()

    override fun getCount(): Int = documentRows.size

    override fun getItem(position: Int): Any = documentRows[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DocumentRowBinding
        if (convertView == null) {
            binding = DocumentRowBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as DocumentRowBinding
        }
        binding.documentRow = getItem(position) as DctDocumentRow

        return binding.root
    }
}