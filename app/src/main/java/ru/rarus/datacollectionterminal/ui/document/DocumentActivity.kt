package ru.rarus.datacollectionterminal.ui.document

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.DOCUMENTID_TAG
import ru.rarus.datacollectionterminal.DOCUMENT_TAG
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityDocumentBinding
import ru.rarus.datacollectionterminal.db.entities.ViewDocument
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
                App.showMessage("???????????????????????? ????????????????")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_document)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[DocumentViewModel::class.java]

        binding.viewpager.adapter = ScreenSlidePagerAdapter(this)
        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = when (position) {
                0 -> resources.getString(R.string.caption_docrows)
                else -> resources.getString(R.string.caption_docheader)
            }
            binding.viewpager.setCurrentItem(tab.position, true)
        }.attach()

        binding.viewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val pageRows = position == 0
                binding.btnScanBarcode.isEnabled = pageRows
                binding.btnClear.isEnabled = pageRows
                binding.btnDeleteSelected.isEnabled = pageRows
                binding.btnContinuousScanning.isEnabled = pageRows
            }
        })

        // ????-?????????????????? ???????????????????? zxing ????????????
        // ???? ?????? ?????????? ???????? ?? ???????????? ???????????????? (bluetooth-????????????)
        binding.btnScanBarcode.setOnClickListener { startScanActivity() }
        binding.btnSaveDocument.setOnClickListener { viewModel.saveDocument() }
        binding.btnDeleteSelected.setOnClickListener { onDeleteClick(null) }
        binding.btnClear.setOnClickListener { onClearClick() }

        if (savedInstanceState == null) {
            viewModel.selectedItems.clear()

            if (intent.extras != null) {
                val id = intent.extras!!.get(DOCUMENTID_TAG)
                if (id != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.getData(id as String).collect {
                            if (it == null)
                                withContext(Dispatchers.Main) { onDataNotFound(id) }
                            else
                                viewModel.document.postValue(it)
                        }
                    }
                } else {
                    val obj = intent.extras!!.get(DOCUMENT_TAG)
                    if ((obj != null) && (obj is ViewDocument)) {
                        val document: ViewDocument = obj
                        viewModel.document.value = document
                    }
                }
            }
        }
    }

    private fun onDataNotFound(id: String) {
        App.showMessage("???????????????? ???? ????????????: $id")
        this.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_select_all)?.isEnabled = binding.viewpager.currentItem == 0
        return super.onPrepareOptionsMenu(menu)

    }

    override fun onBackPressed() {
        if (!viewModel.document.value?.saved!!) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("???????????????? ?????????????????")
                .setPositiveButton("????") { _, _ ->
                    viewModel.saveDocument()
                    super.onBackPressed()
                }
                .setNegativeButton("??????") { dialog, _ ->
                    dialog.dismiss()
                    super.onBackPressed()
                }
            val alert = builder.create()
            alert.show()
        } else super.onBackPressed()
    }

    fun onSettingsClick(@Suppress("UNUSED_PARAMETER")menuItem: MenuItem) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    fun onSelectAllClick(@Suppress("UNUSED_PARAMETER")menuItem: MenuItem) {
        if (binding.viewpager.currentItem == 0) {
            val fragment = supportFragmentManager.findFragmentByTag("f0")
            if (fragment != null) {
                val rowsFragment = fragment as DocumentRowsFragment
                viewModel.selectedItems.clear()
                rowsFragment.adapter.documentRows.forEach {
                    viewModel.selectedItems.add(it.id)
                }
                rowsFragment.adapter.notifyDataSetChanged()
            }
        }
    }


    fun onDeleteClick(@Suppress("UNUSED_PARAMETER")menuItem: MenuItem?) {
        if (viewModel.selectedItems.size > 0) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("?????????????? ?????????????????? ?????????????")
                .setPositiveButton("????") { _, _ ->
                    viewModel.deleteSelectedRows()
                }
                .setNegativeButton("??????") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun onClearClick() {
        if (viewModel.selectedItems.size > 0) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("?????????????? ?????? ?????????????")
                .setPositiveButton("????") { _, _ ->
                    viewModel.clearRows()
                }
                .setNegativeButton("??????") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun startScanActivity() {
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