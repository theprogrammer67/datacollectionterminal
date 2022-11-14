package ru.rarus.datacollectionterminal.ui.document

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.zxing.client.android.BeepManager
import com.google.zxing.integration.android.IntentIntegrator
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.DOCUMENT_TAG
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityDocumentBinding
import ru.rarus.datacollectionterminal.db.ViewDocument
import ru.rarus.datacollectionterminal.ui.BarcodeCaptureActivity
import ru.rarus.datacollectionterminal.ui.ExtBarcodeCaptureActivity
import ru.rarus.datacollectionterminal.ui.SettingsActivity
import ru.rarus.datacollectionterminal.ui.scanner.ScannerActivity


class DocumentActivity() : AppCompatActivity() {
    lateinit var viewModel: DocumentViewModel
    private lateinit var binding: ActivityDocumentBinding

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val barcode: String = data?.getStringExtra("barcode") ?: ""
                val extBarcode: String = data?.getStringExtra("extBarcode") ?: ""
                viewModel.onScanBarcode(barcode, extBarcode)
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                App.showMessage("Сканирование отменено")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_document)

        viewModel = ViewModelProvider(this).get(DocumentViewModel::class.java)

        binding.viewpager.adapter = ScreenSlidePagerAdapter(this)
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> resources.getString(R.string.caption_docrows)
                else -> resources.getString(R.string.caption_docheader)
            }
            binding.viewpager.setCurrentItem(tab.position, true)
        }.attach()

        // По-умолчанию используем zxing сканер
        // Но тут могут быть и другие варианты (bluetooth-сканер)
        binding.btnScanBarcode.setOnClickListener { startScanActivity(false) }
        binding.btnSaveDocument.setOnClickListener { viewModel.saveDocument() }

        if (intent.extras != null) {
            val obj = intent.extras!!.get(DOCUMENT_TAG)
            if ((obj != null) && (obj is ViewDocument)) {
                val document: ViewDocument = obj
                viewModel.document.value = document
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun onSettingsClick(menuItem: MenuItem) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    fun onDeleteClick(menuItem: MenuItem) {
        viewModel.deleteSelectedRows()
    }

    private fun startScanActivity(addBarcode: Boolean) {
        val intent = Intent(this, ScannerActivity::class.java)
        resultLauncher.launch(intent)
    }

    private inner class ScreenSlidePagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DocumentRowsFragment()
                else -> DocumentHeaderFragment()
            }
        }

    }

}