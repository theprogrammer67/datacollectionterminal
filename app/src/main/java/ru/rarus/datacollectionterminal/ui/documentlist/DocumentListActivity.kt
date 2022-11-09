package ru.rarus.datacollectionterminal.ui.documentlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.*
import ru.rarus.datacollectionterminal.databinding.ActivityDocumentListBinding
import ru.rarus.datacollectionterminal.databinding.DocumentItemBinding
import ru.rarus.datacollectionterminal.db.DocumentHeader
import ru.rarus.datacollectionterminal.db.ViewDocument
import ru.rarus.datacollectionterminal.ui.document.DocumentActivity
import java.io.Serializable
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
                viewModel.getDocument(item.id).observeOnce(this) {
                    onGetDocument(it)
                }
            }

        viewModel.documents.observe(this) {
            adapter.documents = it
            adapter.notifyDataSetChanged()
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, DocumentActivity::class.java))
        }

        if (savedInstanceState == null) viewModel.getData()
    }

    private fun onGetDocument(document: ViewDocument?) {
        if (document != null) {
            val intent = Intent(this, DocumentActivity::class.java)
            intent.putExtra(DOCUMENT_TAG, document as Serializable)
            startActivity(intent)
        } else App.showMessage("Документ не найден в БД")
    }
}

class DocumentListAdapter(
    private val context: Context
) : BaseAdapterEx() {
    var documents: List<DocumentHeader> = ArrayList()

    override fun getCount(): Int = documents.size

    override fun getItem(position: Int): Any = documents[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DocumentItemBinding

        if (convertView == null) {
            binding = DocumentItemBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding

            setItemBgColor(position, binding.root)
        } else
            binding = convertView.tag as DocumentItemBinding

        val currentItem = getItem(position) as DocumentHeader
        binding.dctDocument = currentItem
        binding.docDate = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date(currentItem.date))

        return binding.root
    }

}
