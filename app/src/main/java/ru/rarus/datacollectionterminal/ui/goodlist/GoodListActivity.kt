package ru.rarus.datacollectionterminal.ui.goodlist

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.BaseAdapterEx
import ru.rarus.datacollectionterminal.GOODID_TAG
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityGoodsListBinding
import ru.rarus.datacollectionterminal.databinding.GoodItemBinding
import ru.rarus.datacollectionterminal.db.Good
import ru.rarus.datacollectionterminal.ui.SettingsActivity
import ru.rarus.datacollectionterminal.ui.good.GoodActivity
import java.util.*

class GoodListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoodsListBinding
    private lateinit var viewModel: GoodListViewModel
    private lateinit var adapter: GoodListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_goods_list)

        viewModel = ViewModelProvider(this).get(GoodListViewModel::class.java)
        if (savedInstanceState == null) {
            viewModel.selectedItems.clear()
        }

        adapter = GoodListAdapter(this, viewModel.selectedItems)
        binding.lvGoods.adapter = adapter

        viewModel.list.observe(this) {
            adapter.goods = it
            adapter.notifyDataSetChanged()
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

    fun onSettingsClick(menuItem: MenuItem) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    fun onSelectAllClick(menuItem: MenuItem) {
        viewModel.selectedItems.clear()
        adapter.goods.forEach {
            viewModel.selectedItems.add(it.id)
        }
        adapter.notifyDataSetChanged()
    }

    fun onClickGood(item: Good) {
        val intent = Intent(this, GoodActivity::class.java)
        intent.putExtra(GOODID_TAG, item.id)
        startActivity(intent)
    }

    fun onDeleteClick(menuItem: MenuItem) {
        if (viewModel.selectedItems.size > 0) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Удалить выбранные товары?")
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


class GoodListAdapter(
    private val activity: GoodListActivity,
    private val selectedItems: ArrayList<String>
) : BaseAdapterEx() {
    var goods: List<Good> = ArrayList()

    override fun getCount(): Int = goods.size

    override fun getItem(position: Int): Any = goods[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: GoodItemBinding

        if (convertView == null) {
            binding = GoodItemBinding.inflate(LayoutInflater.from(activity), parent, false)
            binding.root.tag = binding

            setItemBgColor(position, binding.root)

            binding.chbSelected.setOnClickListener {
                val checked = (it as CheckBox).isChecked
                if (checked) {
                    selectedItems.add(goods[position].id);
                } else {
                    selectedItems.remove(goods[position].id);
                }
            }
            binding.itmMaster.setOnClickListener {
                activity.onClickGood(goods[position])
            }
        } else
            binding = convertView.tag as GoodItemBinding

        binding.good = getItem(position) as Good
        binding.checked = selectedItems.contains(goods[position].id)

        return binding.root
    }

}