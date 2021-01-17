package ru.rarus.datacollectionterminal.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.databinding.ActivityMainBinding
import ru.rarus.datacollectionterminal.ui.document.DocumentActivity
import ru.rarus.datacollectionterminal.ui.documentlist.DocumentListActivity


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

        binding.btnDocumentList.setOnClickListener {
            startActivity(Intent(this, DocumentListActivity::class.java))
        }

        binding.swServerStart.isChecked = App.restserver.started
        binding.swServerStart.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) App.restserver.start(8118)
            else App.restserver.stop()
        }
    }
}