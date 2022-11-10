package ru.rarus.datacollectionterminal.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.SERVER_PORT
import ru.rarus.datacollectionterminal.databinding.ActivityMainBinding
import ru.rarus.datacollectionterminal.ui.document.DocumentActivity
import ru.rarus.datacollectionterminal.ui.documentlist.DocumentListActivity
import ru.rarus.datacollectionterminal.ui.goodlist.GoodListActivity


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

        binding.btnGoodList.setOnClickListener {
            startActivity(Intent(this, GoodListActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.swServerStart.isChecked = App.prefs.getBoolean("startServer", false)
        App.restserver.state.observe(this) { binding.swServerStart.isChecked = it }
        binding.swServerStart.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) App.restserver.start(SERVER_PORT)
            else App.restserver.stop()
        }
    }
}