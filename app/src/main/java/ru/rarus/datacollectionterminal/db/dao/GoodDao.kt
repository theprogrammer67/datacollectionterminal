package ru.rarus.datacollectionterminal.db.dao

import androidx.room.*
import ru.rarus.datacollectionterminal.db.entities.Good
import ru.rarus.datacollectionterminal.db.entities.GoodAndUnit
import ru.rarus.datacollectionterminal.db.entities.Unit
import ru.rarus.datacollectionterminal.db.entities.ViewGood

@Dao
abstract class GoodDao {

    @Query("SELECT * FROM good ORDER BY name ASC")
    abstract fun getAllGoods(): List<Good>

    @Query("SELECT * FROM good WHERE id = :id")
    abstract fun getGood(id: String): Good?

    @Query("DELETE FROM good  WHERE id IN (:id)")
    abstract fun deleteGoods(id: List<String>)

    @Query("DELETE FROM good")
    abstract fun deleteAllGoods()

    @Query("SELECT * FROM unit WHERE good = :id")
    abstract fun getGoodUnits(id: String): List<Unit>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertGood(good: Good): Long

    @Update
    abstract fun updateGood(good: Good): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertGoods(goods: List<Good>): List<Long>

    @Update
    abstract fun updateGoods(goods: List<Good>): Int

    @Transaction
    open fun saveGood(good: Good) {
        if (insertGood(good) == -1L)
            updateGood(good)
    }

    @Transaction
    open fun saveGoods(goods: List<Good>) {
        val insertResult = insertGoods(goods)
        val updateList = mutableListOf<Good>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(goods[i])
        }

        if (updateList.isNotEmpty()) updateGoods(updateList)
    }

    @Query("SELECT * FROM unit WHERE barcode = :barcode")
    abstract fun getUnit(barcode: String): Unit?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertUnit(unit: Unit): Long

    @Update
    abstract fun updateUnit(unit: Unit): Int

    @Query("DELETE FROM unit WHERE barcode IN (:barcode)")
    abstract fun deleteUnit(barcode: List<String>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertUnits(units: List<Unit>): List<Long>

    @Update
    abstract fun updateUnits(units: List<Unit>): Int

    open fun saveUnit(unit: Unit) {
        if (insertUnit(unit) == -1L)
            updateUnit(unit)
    }

    @Transaction
    open fun saveUnits(units: List<Unit>) {
        val insertResult = insertUnits(units)
        val updateList = mutableListOf<Unit>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(units[i])
        }

        if (updateList.isNotEmpty()) updateUnits(updateList)
    }
}