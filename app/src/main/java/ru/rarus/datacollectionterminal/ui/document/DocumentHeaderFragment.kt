package ru.rarus.datacollectionterminal.ui.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.FragmentDocumentHeaderBinding
import java.text.SimpleDateFormat


class DocumentHeaderFragment : Fragment() {
    private lateinit var binding: FragmentDocumentHeaderBinding
    private lateinit var viewModel: DocumentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_document_header, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(DocumentViewModel::class.java)

        if (viewModel.document.value != null) {
            binding.dctDocument = viewModel.document.value!!.document
            binding.docDate =
                SimpleDateFormat("dd.MM.yyyy HH:mm").format(viewModel.document.value!!.document.date)
        }
    }
}