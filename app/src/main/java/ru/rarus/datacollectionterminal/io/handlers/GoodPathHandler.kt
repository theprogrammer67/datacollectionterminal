package ru.rarus.datacollectionterminal.io.handlers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.rarus.datacollectionterminal.db.entities.ViewGood
import ru.rarus.datacollectionterminal.db.models.GoodModel
import ru.rarus.datacollectionterminal.io.Errors
import ru.rarus.datacollectionterminal.io.HTTP_CODE_BAD_REQUEST
import ru.rarus.datacollectionterminal.io.BaseRestServer

const val GOOD_PATH = "/good"

class GoodPathHandler(server: BaseRestServer) : BasePathHandler(server, GOOD_PATH) {

    override fun onMethGet(id: String): Any? {
        return if (id == "") {
            GoodModel.getAllViewGoods()
        } else {
            GoodModel.getViewGood(id)
        }
    }

    override fun onMethDelete(id: String) {
        if (id == "") {
            GoodModel.deleteAllGoods()
        } else {
            GoodModel.deleteGood(id)
        }
    }

    override fun onMethPost(json: String, id: String) {
        val goodList: List<ViewGood>
        val listType = object : TypeToken<List<ViewGood>>() {}.type
        try {
            goodList = Gson().fromJson(json, listType)
        } catch (e: Exception) {
            throw Errors.createHttpException(HTTP_CODE_BAD_REQUEST)
        }
        GoodModel.saveViewGoods(goodList)
    }

}