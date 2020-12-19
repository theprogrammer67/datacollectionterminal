package ru.rarus.datacollectionterminal.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.rarus.datacollectionterminal.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    //    val REQUEST_CODE = 1
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDocument.setOnClickListener {
            startActivity(Intent(this, DocumentActivity::class.java))
        }
    }
}