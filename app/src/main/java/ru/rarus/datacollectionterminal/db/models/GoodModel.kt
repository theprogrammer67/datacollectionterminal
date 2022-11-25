package ru.rarus.datacollectionterminal.db.models

import androidx.room.Transaction
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.entities.Good
import ru.rarus.datacollectionterminal.db.entities.GoodAndUnit
import ru.rarus.datacollectionterminal.db.entities.ViewGood

class GoodModel {
    companion object {
        @JvmStatic
        fun getAllGoods(): List<Good> {
            return App.database.getGoodDao().getAllGoods()
        }

        @JvmStatic
        fun deleteGood(id: String) {
            App.database.getGoodDao().deleteGoods(listOf(id))
        }

        @JvmStatic
        fun deleteAllGoods() {
            App.database.getGoodDao().deleteAllGoods()
        }

        @JvmStatic
        fun deleteUnit(barcode: List<String>) {
            App.database.getGoodDao().deleteUnit(barcode)
        }

        @JvmStatic
        fun getAllViewGoods(): List<ViewGood> {
            val viewGoods: MutableList<ViewGood> = ArrayList()
            val dao = App.database.getGoodDao()
            val goods = dao.getAllGoods()

            goods.forEach {
                viewGoods.add(ViewGood(it, dao.getGoodUnits(it.id), true))
            }

            return viewGoods
        }

        @JvmStatic
        fun getViewGood(id: String): ViewGood? {
            val dao = App.database.getGoodDao()
            val good = dao.getGood(id) ?: return null
            val units = dao.getGoodUnits(id)
            return ViewGood(good, units, true)
        }

        @JvmStatic
        fun saveViewGood(item: ViewGood) {
            val dao = App.database.getGoodDao()
            App.database.runInTransaction {
                dao.saveGood(item.good)
                dao.saveUnits(item.units)
            }
            item.saved = true
        }

        @JvmStatic
        fun saveViewGoods(items: List<ViewGood>) {
            val dao = App.database.getGoodDao()
            val goods = mutableListOf<Good>()
            val units = mutableListOf<ru.rarus.datacollectionterminal.db.entities.Unit>()
            items.forEach {
                goods.add(it.good)
                units.addAll(it.units)
            }
            App.database.runInTransaction {
                dao.saveGoods(goods)
                dao.saveUnits(units)
            }
            items.forEach { it.saved = true }
        }

        @JvmStatic
        fun getGoodAndUnit(barcode: String): GoodAndUnit? {
            val dao = App.database.getGoodDao()
            val unit = dao.getUnit(barcode) ?: return null
            val good = dao.getGood(unit.good) ?: return null
            return GoodAndUnit(good, unit)
        }

        @JvmStatic
        fun insertGoodAndUnit(goodAndUnit: GoodAndUnit) {
            val dao = App.database.getGoodDao()
            App.database.runInTransaction {
                dao.insertGood(goodAndUnit.good)
                dao.insertUnit(goodAndUnit.unit)
            }
        }
    }
}