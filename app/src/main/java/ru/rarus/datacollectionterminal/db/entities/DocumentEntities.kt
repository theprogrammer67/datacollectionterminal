package ru.rarus.datacollectionterminal.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.R
import java.io.Serializable
import java.util.*

@Entity(tableName = "document")
class DocumentHeader : Serializable {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var number: String = ""
    var date: Long = System.currentTimeMillis()
    var source: Int = 0
    var state: Int = 0
    var description: String = App.context.getString(R.string.new_doc_descriprion)
}

@Entity(
    tableName = "document_row", foreignKeys = [
        ForeignKey(
            entity = DocumentHeader::class,
            parentColumns = ["id"],
            childColumns = ["document"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Unit::class,
            parentColumns = ["barcode"],
            childColumns = ["unit"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)

open class DocumentRow : Serializable {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()

    @ColumnInfo(index = true)
    var document: String = ""
    var unit: String = ""
    var addBarcode: String = ""
    var quantityDoc: Double = 0.0
    var quantityActual: Double = 0.0
    var difference: Double = 0.0
}

/* additional classes */

class ViewDocument() : Serializable {
    var document: DocumentHeader = DocumentHeader()
    var rows: MutableList<ViewDocumentRow> = ArrayList()
    var saved: Boolean = false

    fun addRow(goodAndUnit: GoodAndUnit, addBarcode: String) {
        var item: ViewDocumentRow? = null
        if (addBarcode.isEmpty())
            item = rows.find { it.unit == goodAndUnit.unit.barcode }
        if (item != null)
            item.quantityActual += 1
        else {
            rows.add(ViewDocumentRow(goodAndUnit, document.id, addBarcode))
        }
    }

    constructor(document: DocumentHeader, rows: List<ViewDocumentRow>) : this() {
        this.document = document
        this.rows = rows.toMutableList()
    }

    constructor(document: DocumentHeader, rows: List<ViewDocumentRow>, saved: Boolean) : this(
        document,
        rows
    ) {
        this.saved = saved
    }
}

class ViewDocumentRow() : DocumentRow() {
    var good = ""
    var goodName = ""
    var unitName = ""
    var unitPrice: Double = 0.0

    constructor(goodAndUnit: GoodAndUnit, documentId: String) : this() {
        document = documentId
        goodName = goodAndUnit.good.name
        unitName = goodAndUnit.unit.name
        unitPrice = goodAndUnit.unit.price
        unit = goodAndUnit.unit.barcode
        quantityActual = 1.0
    }

    constructor(
        goodAndUnit: GoodAndUnit,
        documentId: String,
        addBarcode: String
    ) : this(goodAndUnit, documentId) {
        this.addBarcode = addBarcode
    }
}
