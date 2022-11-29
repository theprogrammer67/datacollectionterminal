package ru.rarus.datacollectionterminal.io.handlers

import ru.rarus.datacollectionterminal.BuildConfig
import ru.rarus.datacollectionterminal.io.Handlers
import ru.rarus.datacollectionterminal.io.RestServer

const val INFO_PATH = "/info"

class InfoPathHandler(server: RestServer) : BasePathHandler(server, INFO_PATH) {

    class AppInfo {
        var version_code: Int = BuildConfig.VERSION_CODE
        var version_name: String = BuildConfig.VERSION_NAME
        var application_id: String = BuildConfig.APPLICATION_ID
    }

    override fun onMethGet(id: String): Any? {
        return AppInfo()
    }
}