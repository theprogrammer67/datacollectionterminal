package ru.rarus.datacollectionterminal.ui.good

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.BaseAdapterEx
import ru.rarus.datacollectionterminal.GOODID_TAG
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityGoodBinding
import ru.rarus.datacollectionterminal.databinding.UnitItemBinding
import ru.rarus.datacollectionterminal.db.Unit
import java.util.*

class GoodActivity : AppCompatActivity() {
    private lateinit var viewModel: GoodViewModel
    private lateinit var binding: ActivityGoodBinding
    private lateinit var adapter: UnitListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_good)

        viewModel = ViewModelProvider(this).get(GoodViewModel::class.java)
        viewModel.activity = this

        adapter = UnitListAdapter(applicationContext)
        binding.lvUnits.adapter = adapter

        viewModel.viewGood.observe(this, {
            binding.good = it.good
            adapter.units = it.units
            adapter.notifyDataSetChanged()
        })

        if (intent.extras != null) {
            viewModel.getData(intent.extras?.getString(GOODID_TAG))
        }
    }
}

class UnitListAdapter(private val context: Context) : BaseAdapterEx() {
    var units: List<Unit> = ArrayList()

    override fun getCount(): Int = units.size

    override fun getItem(position: Int): Any = units[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: UnitItemBinding

        if (convertView == null) {
            binding = UnitItemBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding

            setItemBgColor(position, binding.root)
        } else
            binding = convertView.tag as UnitItemBinding

        binding.unit = getItem(position) as Unit

        return binding.root
    }
}