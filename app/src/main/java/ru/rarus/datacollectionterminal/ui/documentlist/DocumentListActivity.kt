package ru.rarus.datacollectionterminal.ui.documentlist

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.*
import ru.rarus.datacollectionterminal.databinding.ActivityDocumentListBinding
import ru.rarus.datacollectionterminal.databinding.DocumentItemBinding
import ru.rarus.datacollectionterminal.db.entities.DocumentHeader
import ru.rarus.datacollectionterminal.ui.SettingsActivity
import ru.rarus.datacollectionterminal.ui.document.DocumentActivity
import java.text.SimpleDateFormat
import java.util.*

class DocumentListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentListBinding
    lateinit var viewModel: DocumentListViewModel
    private lateinit var adapter: DocumentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_document_list)

        viewModel = ViewModelProvider(this)[DocumentListViewModel::class.java]

        if (savedInstanceState == null) {
            viewModel.selectedItems.clear()
        }
        adapter = DocumentListAdapter(this, viewModel.selectedItems)
        binding.lvDocuments.adapter = adapter

        viewModel.list.observe(this) {
            adapter.documents = it
            adapter.notifyDataSetChanged()
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, DocumentActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun onSettingsClick(@Suppress("UNUSED_PARAMETER")menuItem: MenuItem) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    fun onSelectAllClick(@Suppress("UNUSED_PARAMETER")menuItem: MenuItem) {
        viewModel.selectedItems.clear()
        adapter.documents.forEach {
            viewModel.selectedItems.add(it.id)
        }
        adapter.notifyDataSetChanged()
    }

    fun onDeleteClick(@Suppress("UNUSED_PARAMETER")menuItem: MenuItem) {
        if (viewModel.selectedItems.size > 0) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("?????????????? ?????????????????? ???????????????????")
                .setPositiveButton("????") { _, _ ->
                    viewModel.deleteSelectedItems()
                }
                .setNegativeButton("??????") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    fun onClickDocument(document: DocumentHeader) {
        val intent = Intent(this, DocumentActivity::class.java)
        intent.putExtra(DOCUMENTID_TAG, document.id)
        startActivity(intent)
    }
}

class DocumentListAdapter(
    private val activity: DocumentListActivity,
    private val selectedItems: ArrayList<String>
) : BaseAdapterEx() {
    var documents: List<DocumentHeader> = ArrayList()

    override fun getCount(): Int = documents.size

    override fun getItem(position: Int): Any = documents[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DocumentItemBinding

        if (convertView == null) {
            binding = DocumentItemBinding.inflate(LayoutInflater.from(activity), parent, false)
            binding.root.tag = binding
            setItemBgColor(position, binding.root)

            binding.chbSelected.setOnClickListener {
                val checked = (it as CheckBox).isChecked
                val item = it.tag as DocumentHeader
                if (checked) {
                    selectedItems.add(item.id);
                } else {
                    selectedItems.remove(item.id);
                }
            }
            binding.itmMaster.setOnClickListener {
                val item = it.tag as DocumentHeader
                activity.onClickDocument(item)
            }
        } else
            binding = convertView.tag as DocumentItemBinding

        val item = getItem(position) as DocumentHeader
        binding.dctDocument = item
        binding.docDate = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date(item.date))
        binding.checked = selectedItems.contains(item.id)
        binding.chbSelected.tag = item
        binding.itmMaster.tag = item

        return binding.root
    }
}
