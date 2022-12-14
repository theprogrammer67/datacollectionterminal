package ru.rarus.datacollectionterminal.ui.document

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.BaseAdapterEx
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.DialogQuantityBinding
import ru.rarus.datacollectionterminal.databinding.DocumentRowBinding
import ru.rarus.datacollectionterminal.databinding.FragmentDocumentRowsBinding
import ru.rarus.datacollectionterminal.db.entities.DocumentRow
import ru.rarus.datacollectionterminal.db.entities.ViewDocumentRow
import ru.rarus.datacollectionterminal.notifyObserver


class DocumentRowsFragment : Fragment() {
    private lateinit var binding: FragmentDocumentRowsBinding
    lateinit var adapter: DocumentRowsAdapter
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

    fun onQuantityClick(row: DocumentRow) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        val binding: DialogQuantityBinding = DialogQuantityBinding.inflate(LayoutInflater.from(context))
        binding.quantity = row.quantityActual

        builder.setView(binding.root)
        builder.setTitle("????????????????????")
        builder.setPositiveButton("????") { _, _ ->
            row.quantityActual = binding.etQuantity.text.toString().toDouble()
            viewModel.document.notifyObserver()
        }
        builder.setNegativeButton(
            "????????????"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}

class DocumentRowsAdapter(
    private val fragment: DocumentRowsFragment,
) : BaseAdapterEx() {
    var documentRows: List<DocumentRow> = ArrayList()
    var selectedRow: DocumentRow? = null

    var viewModel: DocumentViewModel? = null

    override fun getCount(): Int = documentRows.size

    override fun getItem(position: Int): Any = documentRows[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DocumentRowBinding
        val viewDocumentRow = getItem(position) as ViewDocumentRow

        if (convertView == null) {
            binding =
                DocumentRowBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
            binding.root.tag = binding
            setItemBgColor(position, binding.root)

            binding.chbSelected.setOnClickListener {
                val checked = (it as CheckBox).isChecked
                val item = it.tag as DocumentRow
                if (checked) {
                    viewModel?.selectedItems?.add(item.id);
                } else {
                    viewModel?.selectedItems?.remove(item.id);
                }
            }
            binding.itmMaster.setOnClickListener {
                val item = it.tag as DocumentRow
                selectedRow = if (selectedRow == item)
                    null
                else
                    item
                notifyDataSetChanged()
            }
            binding.lyEditQuantity.btnDec.setOnClickListener {
                val item = it.tag as DocumentRow
                viewModel?.incRowQuantity(item, -1)
            }
            binding.lyEditQuantity.btnInc.setOnClickListener {
                val item = it.tag as DocumentRow
                viewModel?.incRowQuantity(item, 1)
            }
            binding.lyEditQuantity.tvQuantity.setOnClickListener {
                val item = it.tag as DocumentRow
                fragment.onQuantityClick(item)
            }
        } else
            binding = convertView.tag as DocumentRowBinding


        val item = getItem(position) as DocumentRow
        binding.checked = viewModel?.selectedItems?.contains(item.id)
        binding.chbSelected.tag = item
        binding.itmMaster.tag = item
        binding.lyEditQuantity.btnInc.tag = item
        binding.lyEditQuantity.btnDec.tag = item
        binding.lyEditQuantity.tvQuantity.tag = item

        binding.viewDocumentRow = viewDocumentRow
        if (item == selectedRow)
            binding.rowDetail.visibility = View.VISIBLE
        else
            binding.rowDetail.visibility = View.GONE

        return binding.root
    }
}