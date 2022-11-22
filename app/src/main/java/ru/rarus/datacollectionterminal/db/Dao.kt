package ru.rarus.datacollectionterminal.db

import androidx.room.*
import ru.rarus.datacollectionterminal.db.entities.*
import ru.rarus.datacollectionterminal.db.entities.Unit
import java.util.*


@Dao
abstract class DctDao : BaseDao {

    // Document

    @Query("SELECT * FROM document ORDER BY date ASC")
    abstract fun getDocumentsSync(): List<DocumentHeader>

    @Query("SELECT * FROM document WHERE id = :id")
    abstract fun getDocumentSync(id: String): DocumentHeader?

    @Transaction
    open fun upsertDocumentSync(documentHeader: DocumentHeader) {
        val id: Long = insertDocumentSync(documentHeader)
        if (id == -1L)
            updateDocumentSync(documentHeader)
    }

    @Query("DELETE FROM document WHERE id = :id")
    abstract fun deleteDocumentSync(id: String)

    @Query("DELETE FROM document")
    abstract fun deleteDocumentsSync()

    // DocumentRow

    @Query("DELETE FROM document_row WHERE document = :id")
    abstract fun deleteDocumentRowsSync(id: String)


    // Unit

    @Query("DELETE FROM unit WHERE barcode = :barcode")
    abstract fun deleteUnitSync(barcode: String)

    @Query("UPDATE unit SET name = :name, price = :price, good = :good WHERE barcode = :barcode")
    abstract fun updateUnitSync(barcode: String, name: String, price: Double, good: String)

    @Query("SELECT * FROM unit WHERE barcode = :barcode")
    abstract suspend fun getUnitByBarcodeSync(barcode: String): Unit?

    @Query("DELETE FROM unit WHERE good = :id")
    abstract fun deleteGoodUnitsSync(id: String)

    @Query("SELECT * FROM unit WHERE good = :id")
    abstract fun getGoodUnitsSync(id: String): List<Unit>

    // Good

    @Query("DELETE FROM good")
    abstract fun deleteGoodsSync()

    @Query("DELETE FROM good  WHERE id = :id")
    abstract fun deleteGoodSync(id: String)

    @Query("SELECT * FROM good ORDER BY name ASC")
    abstract fun getGoodsSync(): List<Good>

    @Query("SELECT * FROM good WHERE id = :id")
    abstract fun getGoodSync(id: String): Good?

    // other...

    @Query(
        """
        SELECT document_row.*, unit.name as unitName, unit.price as unitPrice, good.name as goodName, good.id as good
        FROM document_row
        LEFT JOIN unit on(document_row.unit = unit.barcode)
        LEFT JOIN good ON (unit.good = good.id)
        WHERE document_row.document = :id
        """
    )
    abstract fun getViewDocumentRowsSync(id: String): List<ViewDocumentRow>


    @Transaction
    open fun upsertGoodsUnitsSync(documentRows: List<ViewDocumentRow>) {
        documentRows.forEach() {
            insertGoodSync(Good(it.good, it.goodName))

            val unit = Unit(it.unit, it.unitName, it.unitPrice, it.good)
            if (insertUnitSync(unit) == -1L) {
                updateUnitSync(it.unit, it.unitName, it.unitPrice, it.good)
            }
        }
    }

    @Transaction
    open fun upsertViewDocumentSync(document: ViewDocument) {
        deleteDocumentRowsSync(document.document.id)
        upsertDocumentSync(document.document)
        insertDocumentRowsSync(document.rows)
        document.saved = true
    }

    @Transaction
    open suspend fun getGoodAndUnitByBarcodeSync(barcode: String): GoodAndUnit? {
        val unit = getUnitByBarcodeSync(barcode) ?: return null
        val good = getGoodSync(unit.good) ?: return null
        return GoodAndUnit(good, unit)
    }

    @Transaction
    open suspend fun getViewDocument(id: String): ViewDocument? {
        val header = getDocumentSync(id) ?: return null
        val rows = getViewDocumentRowsSync(id)
        return ViewDocument(header, rows, true)
    }

    @Transaction
    open suspend fun insertGoodAndUnit(goodAndUnit: GoodAndUnit) {
        insertGoodSync(goodAndUnit.good)
        insertUnit(goodAndUnit.unit)
    }

    @Transaction
    open fun getViewGoodSync(id: String): ViewGood? {
        val good = getGoodSync(id) ?: return null
        val units = getGoodUnitsSync(id)
        return ViewGood(good, units, true)
    }

    @Transaction
    open fun getViewGoodsSync(): List<ViewGood> {
        val viewGoods: MutableList<ViewGood> = ArrayList()
        val goods = getGoodsSync()

        for (good in goods) {
            viewGoods.add(ViewGood(good, getGoodUnitsSync(good.id).toMutableList()))
        }

        return viewGoods
    }

    @Transaction
    open fun insertViewGoodSync(good: ViewGood) {
//        deleteGoodUnitsSync(good.good.id)
        insertGoodSync(good.good)
        insertUnitsSync(good.units)
    }

    open fun insertViewGoodsSync(goodList: List<ViewGood>) {
        goodList.forEach() {
            insertViewGoodSync(it)
        }
    }

    open fun upsertViewDocumentsSync(documentList: List<ViewDocument>) {
        documentList.forEach() {
            upsertViewDocumentSync(it)
        }
    }


}