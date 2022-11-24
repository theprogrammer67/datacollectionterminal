package ru.rarus.datacollectionterminal.db.models

import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.entities.Good
import ru.rarus.datacollectionterminal.db.entities.ViewGood
import java.util.ArrayList

class GoodModel {
    companion object {
        @JvmStatic
        fun getAllGoods(): List<Good> {
            return App.database.getDao().getGoodsSync()
        }

        @JvmStatic
        fun deleteGood(id: String) {
            App.database.getDao().deleteGoodSync(id)
        }

        @JvmStatic
        fun deleteAllGoods() {
            App.database.getDao().deleteGoodsSync()
        }

        @JvmStatic
        fun getAllViewGoods(): List<ViewGood> {
            val viewGoods: MutableList<ViewGood> = ArrayList()
            val dao = App.database.getDao()
            val goods = dao.getGoodsSync()

            goods.forEach {
                viewGoods.add(ViewGood(it, dao.getGoodUnitsSync(it.id)))
            }

            return viewGoods
        }

        @JvmStatic
        fun getViewGood(id: String): ViewGood? {
            val dao = App.database.getDao()
            val good = dao.getGoodSync(id) ?: return null
            val units = dao.getGoodUnitsSync(id)
            return ViewGood(good, units, true)
        }

        @JvmStatic
        fun saveViewGood(item: ViewGood) {
            val dao = App.database.getGoodDao()
            App.database.runInTransaction {
                dao.saveGood(item.good)
                dao.saveUnits(item.units)
            }
        }

    }
}