package ru.rarus.datacollectionterminal.ui.good

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.databinding.ActivityGoodBinding
import ru.rarus.datacollectionterminal.ui.document.DocumentViewModel

class GoodActivity : AppCompatActivity() {
    lateinit var viewModel: GoodViewModel
    private lateinit var binding: ActivityGoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_good)

        viewModel = ViewModelProvider(this).get(GoodViewModel::class.java)
        viewModel.activity = this

    }
}