package ru.rarus.datacollectionterminal

import android.app.Application
import android.content.res.Resources
import android.util.Log
import android.widget.Toast

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
        _resources = resources
        _database = AppDatabase.getInstance(this)
        _restserver = RestServer()
        _restserver.start(8118)
    }

    companion object {
        private lateinit var _resources: Resources
        private lateinit var app: App
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
            Toast.makeText(app, data, Toast.LENGTH_SHORT).show()
        }
    }
}
