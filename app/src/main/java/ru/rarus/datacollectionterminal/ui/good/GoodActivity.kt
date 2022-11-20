package ru.rarus.datacollectionterminal.ui.good

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.BaseAdapterEx
import ru.rarus.datacollectionterminal.GOODID_TAG
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityGoodBinding
import ru.rarus.datacollectionterminal.databinding.UnitItemBinding
import ru.rarus.datacollectionterminal.db.DocumentRow
import ru.rarus.datacollectionterminal.db.Unit
import ru.rarus.datacollectionterminal.ui.SettingsActivity
import java.util.*

class GoodActivity : AppCompatActivity() {
    private lateinit var viewModel: GoodViewModel
    private lateinit var binding: ActivityGoodBinding
    private lateinit var adapter: UnitListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_good)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this).get(GoodViewModel::class.java)
        if (savedInstanceState == null) {
            viewModel.selectedItems.clear()
        }

        adapter = UnitListAdapter(this, viewModel.selectedItems)
        binding.lvUnits.adapter = adapter

        viewModel.viewGood.observe(this) {
            binding.good = it.good
            adapter.units = it.units
            adapter.notifyDataSetChanged()
        }

        if (intent.extras != null) {
            viewModel.getData(intent.extras?.getString(GOODID_TAG))
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun onSelectAllClick(menuItem: MenuItem) {
        viewModel.selectedItems.clear()
        adapter.units.forEach {
            viewModel.selectedItems.add(it.barcode)
        }
        adapter.notifyDataSetChanged()
    }

    fun onSettingsClick(menuItem: MenuItem) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    fun onDeleteClick(menuItem: MenuItem) {
        if (viewModel.selectedItems.size > 0) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Удалить выбранные единицы?")
                .setPositiveButton("Да") { _, _ ->
                    viewModel.deleteSelectedItems()
                }
                .setNegativeButton("Нет") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

}

class UnitListAdapter(
    private val activity: GoodActivity,
    private val selectedItems: ArrayList<String>
) : BaseAdapterEx() {
    var units: List<Unit> = ArrayList()

    override fun getCount(): Int = units.size

    override fun getItem(position: Int): Any = units[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: UnitItemBinding

        if (convertView == null) {
            binding = UnitItemBinding.inflate(LayoutInflater.from(activity), parent, false)
            binding.root.tag = binding
            setItemBgColor(position, binding.root)

            binding.chbSelected.setOnClickListener {
                val checked = (it as CheckBox).isChecked
                val item = it.tag as Unit
                if (checked) {
                    selectedItems.add(item.barcode);
                } else {
                    selectedItems.remove(item.barcode);
                }
            }
        } else
            binding = convertView.tag as UnitItemBinding

        val item = getItem(position) as Unit
        binding.chbSelected.tag = item
        binding.unit = item
        if (item.baseUnit)
            binding.llBaseUnit.visibility = View.VISIBLE
        else
            binding.llBaseUnit.visibility = View.INVISIBLE

        return binding.root
    }
}