package ru.rarus.datacollectionterminal

import android.app.Application
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import ru.rarus.datacollectionterminal.db.AppDatabase
import ru.rarus.datacollectionterminal.io.RestServer

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        _context = this
        _resources = resources
        _database = AppDatabase.getInstance(this)
        _restserver = RestServer()
        _restserver.start(8118)
    }

    companion object {
        private lateinit var _resources: Resources
        private lateinit var _context: App
        val context get() = _context
        private lateinit var _database: AppDatabase
        val database get() = _database
        private lateinit var _restserver: RestServer

        val restserver get() = _restserver
        val resources get() = _resources

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
