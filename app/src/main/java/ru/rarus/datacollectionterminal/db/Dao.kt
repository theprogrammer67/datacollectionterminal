package ru.rarus.datacollectionterminal.db

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
abstract class DctDao {
    @Query("SELECT * FROM document ORDER BY date ASC")
    abstract fun getDocumentsSync(): List<DocumentHeader>

    @Query("SELECT * FROM document WHERE id = :id")
    abstract fun getDocumentSync(id: String): DocumentHeader?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertDocument(documentHeader: DocumentHeader)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertDocumentRows(documentRows: List<DocumentRow>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertDocumentRowsSync(documentRows: List<DocumentRow>)

    @Transaction
    open fun updateViewDocumentSync(document: ViewDocument) {
        deleteDocumentRowsSync(document.document.id)
        updateDocumentSync(document.document)
        insertDocumentRowsSync(document.rows)
    }

    @Transaction
    open suspend fun insertViewDocument(document: ViewDocument) {
        insertDocument(document.document)
        insertDocumentRows(document.rows)
    }

    @Update
    abstract fun updateDocumentSync(document: DocumentHeader)

    @Query("DELETE FROM document WHERE id = :id")
    abstract fun deleteDocumentSync(id: String)

    @Query("DELETE FROM document")
    abstract fun deleteDocumentsSync()

    @Query("DELETE FROM document_row WHERE document = :id")
    abstract fun deleteDocumentRowsSync(id: String)

    @Query("SELECT * FROM unit WHERE barcode = :barcode")
    abstract fun getUnitByBarcode(barcode: String): LiveData<Unit>

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
    open suspend fun getViewDocument(id: String): ViewDocument? {
        val header = getDocumentSync(id) ?: return null
        val rows = getViewDocumentRowsSync(id)
        return ViewDocument(header, rows)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertUnit(unit: Unit)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertUnitsSync(unit: List<Unit>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertGoodSync(good: Good)

    @Transaction
    open suspend fun insertGoodAndUnit(goodAndUnit: GoodAndUnit) {
        insertGoodSync(goodAndUnit.good)
        insertUnit(goodAndUnit.unit)
    }

    @Query("SELECT * FROM good ORDER BY name ASC")
    abstract fun getGoodsSync(): List<Good>

    @Query("SELECT * FROM good WHERE id = :id")
    abstract fun getGoodSync(id: String): Good?

    @Query("SELECT * FROM unit WHERE good = :id")
    abstract fun getGoodUnitsSync(id: String): List<Unit>

    @Transaction
    open fun getViewGoodSync(id: String): ViewGood? {
        val good = getGoodSync(id) ?: return null
        val units = getGoodUnitsSync(id)
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
    abstract fun updateGoodSync(good: Good)

    @Query("DELETE FROM unit WHERE good = :id")
    abstract fun deleteGoodUnitsSync(id: String)

    @Transaction
    open fun updateViewGoodSync(good: ViewGood) {
        deleteGoodUnitsSync(good.good.id)
        updateGoodSync(good.good)
        insertUnitsSync(good.units)
    }

    open fun updateViewGoodsSync(goodList: List<ViewGood>) {
        goodList.forEach() {
            updateViewGoodSync(it)
        }
    }

    open fun updateViewDocumentsSync(documentList: List<ViewDocument>) {
        documentList.forEach() {
            updateViewDocumentSync(it)
        }
    }


}