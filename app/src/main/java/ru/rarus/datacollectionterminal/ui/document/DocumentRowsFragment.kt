package ru.rarus.datacollectionterminal.ui.document

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.BaseAdapterEx
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.DocumentRowBinding
import ru.rarus.datacollectionterminal.databinding.FragmentDocumentRowsBinding
import ru.rarus.datacollectionterminal.db.DocumentRow
import ru.rarus.datacollectionterminal.db.ViewDocumentRow


class DocumentRowsFragment : Fragment() {
    private lateinit var binding: FragmentDocumentRowsBinding
    private lateinit var adapter: DocumentRowsAdapter
    private lateinit var viewModel: DocumentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_document_rows, container, false)

        adapter = DocumentRowsAdapter(this.context)
        binding.lvRows.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(DocumentViewModel::class.java)

        viewModel.document.observe(this, {
            adapter.documentRows = it.rows
            adapter.notifyDataSetChanged()
        })
    }
}

class DocumentRowsAdapter(private val context: Context?) : BaseAdapterEx() {
    var documentRows: List<DocumentRow> = ArrayList()
    var selectedRow: Int = -1

    override fun getCount(): Int = documentRows.size

    override fun getItem(position: Int): Any = documentRows[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DocumentRowBinding
        val viewDocumentRow = getItem(position) as ViewDocumentRow

        if (convertView == null) {
            binding = DocumentRowBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
            binding.chbSelected.setOnClickListener {
                viewDocumentRow.isSelected = (it as CheckBox).isChecked
            }
            binding.rowMaster.setOnClickListener {
                selectedRow = if (selectedRow == position)
                    -1
                else
                    position
                notifyDataSetChanged()
            }
            binding.lyEditQuantity.btnDec.setOnClickListener {
                viewDocumentRow.quantityActual--
                notifyDataSetChanged()
            }
            binding.lyEditQuantity.btnInc.setOnClickListener {
                viewDocumentRow.quantityActual++
                notifyDataSetChanged()
            }
            setItemBgColor(position, binding.root)
        } else
            binding = convertView.tag as DocumentRowBinding

        binding.viewDocumentRow = viewDocumentRow
        if (position == selectedRow)
            binding.rowDetail.visibility = View.VISIBLE
        else
            binding.rowDetail.visibility = View.GONE

        return binding.root
    }
}