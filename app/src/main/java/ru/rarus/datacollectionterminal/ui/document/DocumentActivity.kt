package ru.rarus.datacollectionterminal.ui.document

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.zxing.integration.android.IntentIntegrator
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.DOCUMENTID_TAG
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityDocumentBinding
import ru.rarus.datacollectionterminal.ui.ScannerCaptureActivity


class DocumentActivity() : AppCompatActivity() {
    lateinit var viewModel: DocumentViewModel
    private lateinit var binding: ActivityDocumentBinding
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_document)

        viewModel = ViewModelProvider(this).get(DocumentViewModel::class.java)
        viewModel.activity = this

        binding.viewpager.adapter = ScreenSlidePagerAdapter(this)
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> resources.getString(R.string.caption_docrows)
                else -> resources.getString(R.string.caption_docheader)
            }
            binding.viewpager.setCurrentItem(tab.position, true)
        }.attach()

        binding.btnScanBarcode.setOnClickListener { viewModel.scanBarcode() }
        binding.btnSaveDocument.setOnClickListener { viewModel.saveDocument() }

        if (intent.extras != null) {
            viewModel.getData(intent.extras?.getString(DOCUMENTID_TAG))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun onDeleteClick(menuItem: MenuItem) {
        viewModel.deleteSelectedRows()
    }

    fun startScanActivity() {
        val integrator = IntentIntegrator(this)
        integrator.captureActivity = ScannerCaptureActivity::class.java
        integrator.setRequestCode(REQUEST_CODE)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null) {
            if (result.contents == null)
                App.showMessage("Сканирование отменено")
            else
                viewModel.onScanBarcode(result.contents)
        } else
            super.onActivityResult(requestCode, resultCode, data)
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