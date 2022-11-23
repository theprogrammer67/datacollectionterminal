package ru.rarus.datacollectionterminal.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import ru.rarus.datacollectionterminal.db.entities.DocumentHeader
import ru.rarus.datacollectionterminal.db.entities.DocumentRow
import ru.rarus.datacollectionterminal.db.entities.Good
import ru.rarus.datacollectionterminal.db.entities.Unit

interface BaseDao {

    // Document

    @Update
    fun updateDocumentSync(document: DocumentHeader): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDocumentSync(document: DocumentHeader): Long

    // DocumentRow

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDocumentRowsSync(documentRows: List<DocumentRow>): List<Long>

    // Unit

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUnitSync(unit: Unit): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUnit(unit: Unit): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUnitsSync(units: List<Unit>): List<Long>

    @Update
    fun updateUnitSync(unit: Unit): Int

    // Good

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGoodSync(good: Good): Long

    @Update
    fun updateGoodSync(good: Good): Int
}