package ru.rarus.datacollectionterminal.ui.documentlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.DOCUMENTID_TAG
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityDocumentListBinding
import ru.rarus.datacollectionterminal.databinding.DocumentItemBinding
import ru.rarus.datacollectionterminal.db.DocumentHeader
import ru.rarus.datacollectionterminal.ui.document.DocumentActivity
import java.text.SimpleDateFormat
import java.util.*

class DocumentListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentListBinding
    private lateinit var viewModel: DocumentListViewModel
    private lateinit var adapter: DocumentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_document_list)

        viewModel = ViewModelProvider(this).get(DocumentListViewModel::class.java)
        viewModel.activity = this

        adapter = DocumentListAdapter(applicationContext)
        binding.lvDocuments.adapter = adapter

        binding.lvDocuments.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val item = parent.getItemAtPosition(position) as DocumentHeader
                val intent = Intent(this, DocumentActivity::class.java)
                intent.putExtra(DOCUMENTID_TAG, item.id)
                startActivity(intent)
            }

        viewModel.documents.observe(this, {
            adapter.documents = it
            adapter.notifyDataSetChanged()
        })

        if (savedInstanceState == null) viewModel.getData()
    }
}

class DocumentListAdapter(
    private val context: Context
) : BaseAdapter() {
    var documents: List<DocumentHeader> = ArrayList()

    override fun getCount(): Int = documents.size

    override fun getItem(position: Int): Any = documents[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DocumentItemBinding

        if (convertView == null) {
            binding = DocumentItemBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
            val currentItem = getItem(position) as DocumentHeader
            binding.docDate = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date(currentItem.date))
        } else
            binding = convertView.tag as DocumentItemBinding

        binding.dctDocument = getItem(position) as DocumentHeader

        return binding.root
    }

}
