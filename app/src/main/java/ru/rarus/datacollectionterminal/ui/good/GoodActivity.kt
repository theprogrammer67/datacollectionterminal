package ru.rarus.datacollectionterminal.ui.good

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.GOODID_TAG
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityGoodBinding

class GoodActivity : AppCompatActivity() {
    lateinit var viewModel: GoodViewModel
    private lateinit var binding: ActivityGoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_good)

        viewModel = ViewModelProvider(this).get(GoodViewModel::class.java)
        viewModel.activity = this

        if (intent.extras != null) {
            viewModel.getData(intent.extras?.getString(GOODID_TAG))
        }

        if (viewModel.viewGood.value != null) {
            binding.good = viewModel.viewGood.value!!.good
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }
}