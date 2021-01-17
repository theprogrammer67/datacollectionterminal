package ru.rarus.datacollectionterminal.ui.goodlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.BaseAdapterEx
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityGoodsListBinding
import ru.rarus.datacollectionterminal.databinding.GoodItemBinding
import ru.rarus.datacollectionterminal.db.Good
import java.util.*

class GoodListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoodsListBinding
    private lateinit var viewModel: GoodListViewModel
    private lateinit var adapter: GoodListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_goods_list)

        viewModel = ViewModelProvider(this).get(GoodListViewModel::class.java)

        adapter = GoodListAdapter(applicationContext)
        binding.lvGoods.adapter = adapter

        viewModel.goods.observe(this, {
            adapter.goods = it
            adapter.notifyDataSetChanged()
        })

        if (savedInstanceState == null) viewModel.getData(this)
    }
}

class GoodListAdapter(
    private val context: Context
) : BaseAdapterEx() {
    var goods: List<Good> = ArrayList()

    override fun getCount(): Int = goods.size

    override fun getItem(position: Int): Any = goods[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: GoodItemBinding

        if (convertView == null) {
            binding = GoodItemBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding

            setItemBgColor(position, binding.root)
        } else
            binding = convertView.tag as GoodItemBinding

        binding.good = getItem(position) as Good

        return binding.root
    }

}