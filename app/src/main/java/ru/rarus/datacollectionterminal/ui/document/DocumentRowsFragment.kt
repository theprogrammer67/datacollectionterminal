package ru.rarus.datacollectionterminal.ui.document

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.DocumentRowBinding
import ru.rarus.datacollectionterminal.databinding.FragmentDocumentRowsBinding
import ru.rarus.datacollectionterminal.db.DocumentRow
import ru.rarus.datacollectionterminal.db.ViewDocument
import ru.rarus.datacollectionterminal.db.ViewDocumentRow


class DocumentRowsFragment : Fragment() {
    private lateinit var binding: FragmentDocumentRowsBinding
    private lateinit var adapter: DocumentRowsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_document_rows, container, false)

        adapter = DocumentRowsAdapter((activity as DocumentActivity).viewModel.document, this)
        binding.lvRows.adapter = adapter

        return binding.root
    }
}

class DocumentRowsAdapter() : BaseAdapter() {
    private var context: Context? = null
    private var documentRows: List<DocumentRow> = ArrayList()

    constructor(document: LiveData<ViewDocument>, fragment: Fragment) : this() {
        this.context = fragment.context
        document.observe(fragment, {
            documentRows = it.rows
            notifyDataSetChanged()
        })
    }

    override fun getCount(): Int = documentRows.size

    override fun getItem(position: Int): Any = documentRows[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DocumentRowBinding

        if (convertView == null) {
            binding = DocumentRowBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
            binding.chbSelected.setOnClickListener {
                (getItem(position) as ViewDocumentRow).isSelected = (it as CheckBox).isChecked
            }
        } else
            binding = convertView.tag as DocumentRowBinding

        binding.viewDocumentRow = getItem(position) as ViewDocumentRow

        return binding.root
    }
}