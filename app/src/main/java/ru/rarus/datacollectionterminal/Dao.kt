package ru.rarus.datacollectionterminal

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class DctDao {
    @Query("SELECT * FROM document ORDER BY date ASC")
    abstract fun getDocuments(): LiveData<List<DctDocumentHeader>>

    @Transaction
    @Query("SELECT * from document")
    abstract fun getDocumentAndRows(): LiveData<List<DocumentAndRows>>

    @Query("SELECT * FROM document WHERE id = :id")
    abstract fun getDocument(id: String): LiveData<DctDocumentHeader>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertDocument(documentHeader: DctDocumentHeader)

    @Query("DELETE FROM document WHERE id = :id")
    abstract suspend fun deleteDocument(id: String)

    @Query("DELETE FROM document")
    abstract suspend fun deleteDocuments()

    @Query("SELECT * FROM unit WHERE barcode = :barcode")
    abstract fun getUnitByBarcode(barcode: String): LiveData<Unit>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertUnit(unit: Unit)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertUnits(unit: List<Unit>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertGood(good: Good)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoodAndUnits(goodAndUnits: GoodAndUnits) {
        if (goodAndUnits.good != null) {
            insertGood(goodAndUnits.good!!)
            insertUnits(goodAndUnits.units)
        }
    }

//    @Query("SELECT * FROM unit WHERE barcode = :barcode")
//    suspend fun getDocRowByCarcode(barcode: String)
}