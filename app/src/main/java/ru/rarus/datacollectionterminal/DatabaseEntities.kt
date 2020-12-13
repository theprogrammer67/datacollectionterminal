package ru.rarus.datacollectionterminal

import androidx.room.*
import java.util.*


@Entity(tableName = "document")
class DctDocumentHeader() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var number: String = ""
    var date: Long = 0
    var source: Int = 0
    var state: Int = 0
}

@Entity(
    tableName = "document_row", foreignKeys = [ForeignKey(
        entity = DctDocumentHeader::class,
        parentColumns = ["id"],
        childColumns = ["document"],
        onDelete = ForeignKey.CASCADE
    )]
)
class DctDocumentRow() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()

    @ColumnInfo(index = true)
    var document: String = ""
    var unit: String = ""
    var addBarcode: String = ""
    var quantityDoc: Double = 0.0
    var quantityActual: Double = 0.0
    var difference: Double = 0.0

    constructor(addBarcode: String) : this() {
        this.addBarcode = addBarcode
        quantityActual = 1.0
    }
}

@Entity(tableName = "good")
class Good() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name: String = "Неизвестный товар"
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

    constructor(barcode: String, good: Good, baseUnit: Boolean): this() {
        this.barcode = barcode
        this.good = good.id
        this.baseUnit = baseUnit
    }
}

data class ViewDocumentRow(
    var goodName: String,
    var unitName: String,
    var unitBarcode: String,
    var addBarcode: String,
    var quantityDoc: Double,
    var quantityActual: Double,
    var difference: Double
)

class DocumentAndRows {
    @Embedded
    var document: DctDocumentHeader? = null

    @Relation(
        parentColumn = "id",
        entityColumn = "document",
        entity = DctDocumentRow::class
    )
    var rows: List<DctDocumentRow> = ArrayList()
}

class GoodAndUnits() {
    @Embedded
    var good: Good? = null

    @Relation(
        parentColumn = "id",
        entityColumn = "good",
        entity = Unit::class
    )
    var units: List<Unit> = ArrayList()

    constructor(good: Good, units: List<Unit>) : this() {
        this.good = good
        this.units = units
    }
    constructor(barcode: String) : this() {
        this.good = Good()
        this.units = listOf(Unit(barcode, good!!, true))
    }
}

data class TestData(
    val id: String,
    val number: Int,
    val string: String,
)
