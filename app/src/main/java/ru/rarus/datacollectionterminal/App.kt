package ru.rarus.datacollectionterminal

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import ru.rarus.datacollectionterminal.db.AppDatabase
import ru.rarus.datacollectionterminal.io.RestServer

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        _context = this
        _resources = resources
        _database = AppDatabase.getInstance(this)
        _restserver = RestServer()
        _prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (_prefs.getBoolean("startServer", false))
            _restserver.start(SERVER_PORT)
    }

    companion object {
        private lateinit var _resources: Resources
        private lateinit var _context: App
        val context get() = _context
        private lateinit var _database: AppDatabase
        val database get() = _database
        private lateinit var _restserver: RestServer
        private lateinit var _prefs: SharedPreferences


        val restserver get() = _restserver
        val resources get() = _resources
        val prefs get() = _prefs

        fun stop() {
            _restserver.stop()
        }

        fun handleError(tag: String, code: Int, description: String) {
            val msg = "Code: ${code}; Descr.: $description"
            showMessage(
                """$tag
                |$msg
            """.trimMargin()
            )
            Log.d(tag, msg)
            print("ERROR! ${tag}: $msg")
        }


        fun showMessage(data: String?) {
            Toast.makeText(_context, data, Toast.LENGTH_SHORT).show()
        }
    }
}
