package ru.rarus.datacollectionterminal.db.dao

import androidx.room.Dao
import androidx.room.Query
import ru.rarus.datacollectionterminal.db.entities.Good
import ru.rarus.datacollectionterminal.db.entities.Unit

@Dao
abstract class GoodDao {

    @Query("SELECT * FROM good ORDER BY name ASC")
    abstract fun getAllGoods(): List<Good>

    @Query("DELETE FROM good  WHERE id IN (:id)")
    abstract fun deleteGoods(id: List<String>)

    @Query("DELETE FROM good")
    abstract fun deleteAllGoods()

    @Query("SELECT * FROM unit WHERE good = :id")
    abstract fun getGoodUnits(id: String): List<Unit>
}