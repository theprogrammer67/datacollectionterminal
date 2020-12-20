package ru.rarus.datacollectionterminal.db

import androidx.lifecycle.LiveData
import androidx.room.*

const val getViewDocumentRowsSyncSql = """
        SELECT document_row.*, unit.name as unitName, good.name as goodName, good.id as good
        FROM document_row
        LEFT JOIN unit on(document_row.unit = unit.barcode)
        LEFT JOIN good ON (unit.good = good.id)
        WHERE document_row.document = :id
        """

@Dao
abstract class DctDao {
    @Query("SELECT * FROM document ORDER BY date ASC")
    abstract fun getDocuments(): LiveData<List<DctDocumentHeader>>

    @Query("SELECT * FROM document ORDER BY date ASC")
    abstract fun getDocumentsSync(): List<DctDocumentHeader>

//    @Transaction
//    @Query("SELECT * from document WHERE id = :id")
//    abstract fun getDocumentAndRows(id: String): LiveData<DocumentAndRows>
//
//    @Transaction
//    @Query("SELECT * from document WHERE id = :id")
//    abstract fun getDocumentAndRowsSync(id: String): DocumentAndRows

    @Query("SELECT * FROM document WHERE id = :id")
    abstract fun getDocument(id: String): LiveData<DctDocumentHeader>

    @Query("SELECT * FROM document WHERE id = :id")
    abstract fun getDocumentSync(id: String): DctDocumentHeader

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertDocument(documentHeader: DctDocumentHeader)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertDocumentRows(documentRows: List<DctDocumentRow>)

//    @Transaction
//    open suspend fun insertDocumentAndRows(documentAndRows: DocumentAndRows) {
//        insertDocument(documentAndRows.document)
//        insertDocumentRows(documentAndRows.rows)
//    }
//
//    @Transaction
//    open suspend fun updateDocumentAndRows(documentAndRows: DocumentAndRows) {
//        deleteDocumentRows(documentAndRows.document.id)
//        updateDocument(documentAndRows.document)
//        insertDocumentRows(documentAndRows.rows)
//    }

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
    @Query("SELECT * FROM good JOIN unit ON(unit.good = good.id) WHERE unit.barcode = :barcode")
    abstract fun getGoodAndUnitByBarcode(barcode: String): LiveData<GoodAndUnit>

    @Query(getViewDocumentRowsSyncSql)
    abstract fun getViewDocumentRows(id: String): LiveData<List<ViewDocumentRow>>

    @Query(getViewDocumentRowsSyncSql)
    abstract fun getViewDocumentRowsSync(id: String): List<ViewDocumentRow>

    @Transaction
    open suspend fun getViewDocument(id: String): ViewDocument {
        val rows = getViewDocumentRowsSync(id)
        val header = getDocumentSync(id)
        return ViewDocument(header, rows)
    }

//    abstract suspend fun insertViewDocumentRows(documentRows: List<ViewDocumentRow>)

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