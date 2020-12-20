package ru.rarus.datacollectionterminal.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class DctDao {
    @Query("SELECT * FROM document ORDER BY date ASC")
    abstract fun getDocuments(): LiveData<List<DctDocumentHeader>>

    @Query("SELECT * FROM document ORDER BY date ASC")
    abstract fun getDocumentsSync(): List<DctDocumentHeader>

    @Transaction
    @Query("SELECT * from document WHERE id = :id")
    abstract fun getDocumentAndRows(id: String): LiveData<DocumentAndRows>

    @Query("SELECT * FROM document WHERE id = :id")
    abstract fun getDocument(id: String): LiveData<DctDocumentHeader>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertDocument(documentHeader: DctDocumentHeader)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertDocumentRows(documentRows: List<DctDocumentRow>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDocumentAndRows(documentAndRows: DocumentAndRows) {
        insertDocument(documentAndRows.document)
        insertDocumentRows(documentAndRows.rows)
    }

    @Transaction
    open suspend fun updateDocumentAndRows(documentAndRows: DocumentAndRows) {
        deleteDocumentRows(documentAndRows.document.id)
        updateDocument(documentAndRows.document)
        insertDocumentRows(documentAndRows.rows)
    }

    @Update
    abstract fun updateDocument(document: DctDocumentHeader)

    @Query("DELETE FROM document WHERE id = :id")
    abstract suspend fun deleteDocument(id: String)

    @Query("DELETE FROM document")
    abstract suspend fun deleteDocuments()

    @Query("DELETE FROM document_row WHERE document = :id")
    abstract suspend fun deleteDocumentRows(id: String)

    @Query("SELECT * FROM unit WHERE barcode = :barcode")
    abstract fun getUnitByBarcode(barcode: String): LiveData<Unit>

    @Transaction
    @Query("SELECT * FROM good JOIN unit on(unit.good = good.id) WHERE unit.barcode = :barcode")
    abstract fun getGoodAndUnitByBarcode(barcode: String): LiveData<GoodAndUnit>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertUnit(unit: Unit)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertUnits(unit: List<Unit>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertGood(good: Good)

    @Transaction
    open suspend fun insertGoodAndUnit(goodAndUnit: GoodAndUnit) {
        insertGood(goodAndUnit.good)
        insertUnit(goodAndUnit.unit)
    }
}