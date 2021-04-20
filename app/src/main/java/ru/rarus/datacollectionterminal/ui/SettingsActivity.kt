package ru.rarus.datacollectionterminal.ui

import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import ru.rarus.datacollectionterminal.R


class SettingsActivity : AppCompatActivity() {

    private fun getIpAddress(): String {
        val wm = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        return Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val tvIpAddress = findViewById<TextView>(R.id.tvIpAddress)
        tvIpAddress.text = getIpAddress()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}