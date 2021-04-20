package ru.rarus.datacollectionterminal.db

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
abstract class DctDao {
    @Query("SELECT * FROM document ORDER BY date ASC")
    abstract fun getDocuments(): LiveData<List<DocumentHeader>>

    @Query("SELECT * FROM document ORDER BY date ASC")
    abstract fun getDocumentsSync(): List<DocumentHeader>

    @Query("SELECT * FROM document WHERE id = :id")
    abstract fun getDocument(id: String): LiveData<DocumentHeader>

    @Query("SELECT * FROM document WHERE id = :id")
    abstract fun getDocumentSync(id: String): DocumentHeader

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertDocument(documentHeader: DocumentHeader)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertDocumentRows(documentRows: List<DocumentRow>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertDocumentRowsSync(documentRows: List<DocumentRow>)

    @Transaction
    open suspend fun updateViewDocument(document: ViewDocument) {
        deleteDocumentRows(document.document.id)
        updateDocument(document.document)
        insertDocumentRows(document.rows)
    }

    @Transaction
    open fun updateViewDocumentSync(document: ViewDocument) {
        deleteDocumentRowsSync(document.document.id)
        updateDocument(document.document)
        insertDocumentRowsSync(document.rows)
    }

    @Transaction
    open suspend fun insertViewDocument(document: ViewDocument) {
        insertDocument(document.document)
        insertDocumentRows(document.rows)
    }

    @Update
    abstract fun updateDocument(document: DocumentHeader)

    @Query("DELETE FROM document WHERE id = :id")
    abstract suspend fun deleteDocument(id: String)

    @Query("DELETE FROM document WHERE id = :id")
    abstract fun deleteDocumentSync(id: String)

    @Query("DELETE FROM document")
    abstract suspend fun deleteDocuments()

    @Query("DELETE FROM document")
    abstract fun deleteDocumentsSync()

    @Query("DELETE FROM document_row WHERE document = :id")
    abstract suspend fun deleteDocumentRows(id: String)

    @Query("DELETE FROM document_row WHERE document = :id")
    abstract fun deleteDocumentRowsSync(id: String)

    @Query("SELECT * FROM unit WHERE barcode = :barcode")
    abstract fun getUnitByBarcode(barcode: String): LiveData<Unit>

    @Transaction
    @Query("SELECT * FROM good JOIN unit ON(unit.good = good.id) WHERE unit.barcode = :barcode")
    abstract fun getGoodAndUnitByBarcode(barcode: String): LiveData<GoodAndUnit>

    @Query(
        """
        SELECT document_row.*, unit.name as unitName, good.name as goodName, good.id as good
        FROM document_row
        LEFT JOIN unit on(document_row.unit = unit.barcode)
        LEFT JOIN good ON (unit.good = good.id)
        WHERE document_row.document = :id
        """
    )
    abstract fun getViewDocumentRowsSync(id: String): List<ViewDocumentRow>

    @Transaction
    open suspend fun getViewDocument(id: String): ViewDocument {
        val rows = getViewDocumentRowsSync(id)
        val header = getDocumentSync(id)
        return ViewDocument(header, rows)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertUnit(unit: Unit)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertUnits(unit: List<Unit>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertUnitsSync(unit: List<Unit>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertGood(good: Good)

    @Transaction
    open suspend fun insertGoodAndUnit(goodAndUnit: GoodAndUnit) {
        insertGood(goodAndUnit.good)
        insertUnit(goodAndUnit.unit)
    }

    @Query("SELECT * FROM good ORDER BY name ASC")
    abstract fun getGoods(): LiveData<List<Good>>

    @Query("SELECT * FROM good ORDER BY name ASC")
    abstract fun getGoodsSync(): List<Good>

    @Query("SELECT * FROM good WHERE id = :id")
    abstract fun getGoodSync(id: String): Good

    @Query("SELECT * FROM unit WHERE good = :id")
    abstract fun getGoodUnitsSync(id: String): List<Unit>

    @Transaction
    open suspend fun getViewGood(id: String): ViewGood {
        val units = getGoodUnitsSync(id)
        val good = getGoodSync(id)
        return ViewGood(good, units)
    }

    @Transaction
    open fun getViewGoodSync(id: String): ViewGood {
        val units = getGoodUnitsSync(id)
        val good = getGoodSync(id)
        return ViewGood(good, units)
    }

    @Transaction
    open fun getViewGoodsSync(): List<ViewGood> {
        var viewGoods: MutableList<ViewGood> = ArrayList()
        val goods = getGoodsSync()

        for (good in goods) {
            viewGoods.add(ViewGood(good, getGoodUnitsSync(good.id).toMutableList()))
        }

        return viewGoods
    }

    @Query("DELETE FROM good")
    abstract fun deleteGoodsSync()

    @Query("DELETE FROM good  WHERE id = :id")
    abstract fun deleteGoodSync(id: String)

    @Update
    abstract fun updateGood(good: Good)

    @Query("DELETE FROM unit WHERE good = :id")
    abstract fun deleteGoodUnitsSync(id: String)

    @Transaction
    open fun updateViewGoodSync(good: ViewGood) {
        deleteGoodUnitsSync(good.good.id)
        updateGood(good.good)
        insertUnitsSync(good.units)
    }

}