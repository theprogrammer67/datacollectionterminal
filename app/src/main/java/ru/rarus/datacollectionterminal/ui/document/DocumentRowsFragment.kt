package ru.rarus.datacollectionterminal.ui.document

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

        adapter = DocumentRowsAdapter(this)
        binding.lvRows.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DocumentViewModel::class.java]
        adapter.viewModel = viewModel

        viewModel.document.observe(viewLifecycleOwner) {
            adapter.documentRows = it.rows
            adapter.notifyDataSetChanged()
        }
    }
}

class DocumentRowsAdapter(
    private val fragment: DocumentRowsFragment,
    ) : BaseAdapterEx() {
    var documentRows: List<DocumentRow> = ArrayList()
    var selectedRow: Int = -1

    var viewModel: DocumentViewModel? = null

    override fun getCount(): Int = documentRows.size

    override fun getItem(position: Int): Any = documentRows[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DocumentRowBinding
        val viewDocumentRow = getItem(position) as ViewDocumentRow

        if (convertView == null) {
            binding = DocumentRowBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
            binding.root.tag = binding

            binding.chbSelected.setOnClickListener {
                val checked = (it as CheckBox).isChecked
                if (checked) {
                    viewModel?.selectedItems?.add(documentRows[position].id);
                } else {
                    viewModel?.selectedItems?.remove(documentRows[position].id);
                }
            }
            binding.rowMaster.setOnClickListener {
                selectedRow = if (selectedRow == position)
                    -1
                else
                    position
                notifyDataSetChanged()
            }
            binding.lyEditQuantity.btnDec.setOnClickListener {
                viewModel?.incRowQuantity(position, -1)
            }
            binding.lyEditQuantity.btnInc.setOnClickListener {
                viewModel?.incRowQuantity(position, 1)
            }

            setItemBgColor(position, binding.root)
        } else
            binding = convertView.tag as DocumentRowBinding

        binding.checked = viewModel?.selectedItems?.contains(documentRows[position].id)
        binding.viewDocumentRow = viewDocumentRow
        if (position == selectedRow)
            binding.rowDetail.visibility = View.VISIBLE
        else
            binding.rowDetail.visibility = View.GONE

        return binding.root
    }
}