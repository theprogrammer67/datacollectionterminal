package ru.rarus.datacollectionterminal.db

import androidx.room.*
import java.io.Serializable
import java.util.*


@Entity(tableName = "document")
class DocumentHeader() : Serializable {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var number: String = ""
    var date: Long = System.currentTimeMillis()
    var source: Int = 0
    var state: Int = 0
    var description: String = "Новый документ"
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
open class DocumentRow() : Serializable {
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

@Entity(tableName = "good")
class Good() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name: String = "Неизвестный товар"

    constructor(barcode: String) : this() {
        name = "Товар $barcode"
    }

    constructor(id: String, name: String) : this() {
        this.id = id
        this.name = name
    }
}

@Entity(
    tableName = "unit", foreignKeys = [ForeignKey(
        entity = Good::class,
        parentColumns = ["id"],
        childColumns = ["good"],
        onDelete = ForeignKey.CASCADE
    )]
)
class Unit() {
    @PrimaryKey
    var barcode: String = ""

    @ColumnInfo(index = true)
    var good: String = ""
    var name: String = "шт"
    var conversionFactor: Double = 1.0
    var price: Double = 0.0
    var baseUnit: Boolean = false

    constructor(barcode: String, good: Good, baseUnit: Boolean) : this() {
        this.barcode = barcode
        this.good = good.id
        this.baseUnit = baseUnit
    }

    constructor(barcode: String, name: String, price: Double, good: String) : this() {
        this.barcode = barcode
        this.name = name
        this.price = price
        this.good = good
        this.baseUnit = true
    }
}

/* additional classes */

class GoodAndUnit() {
    @Embedded
    var good: Good = Good()

    @Relation(
        parentColumn = "id",
        entityColumn = "good",
        entity = Unit::class
    )
    var unit: Unit = Unit()

    constructor(barcode: String) : this() {
        this.good = Good(barcode)
        this.unit = Unit(barcode, good, true)
    }
}

class ViewGood() {
    var good: Good = Good()
    var units: MutableList<Unit> = ArrayList()
    var saved: Boolean = false

    constructor(good: Good, units: List<Unit>) : this() {
        this.good = good
        this.units = units.toMutableList()
    }

    constructor(good: Good, units: List<Unit>, saved: Boolean) : this(good, units) {
        this.saved = saved
    }
}

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

    @Ignore
    @Transient
    var isSelected = false

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
