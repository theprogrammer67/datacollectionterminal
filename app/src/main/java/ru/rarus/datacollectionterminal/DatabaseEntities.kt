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

    fun clear() {
        id = UUID.randomUUID().toString()
        number = ""
        date = 0
        source = 0
        state = 0
    }
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

    constructor(goodAndUnit: GoodAndUnit) : this() {
        unit = goodAndUnit.unit.barcode
        quantityActual = 1.0
    }
    constructor(viewDocumentRow: ViewDocumentRow, documentId: String) : this() {
        document = documentId
        unit = viewDocumentRow.unitBarcode
        addBarcode = viewDocumentRow.addBarcode
        quantityDoc = viewDocumentRow.quantityDoc
        quantityActual = viewDocumentRow.quantityActual
        difference = viewDocumentRow.difference
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

    constructor(barcode: String, good: Good, baseUnit: Boolean) : this() {
        this.barcode = barcode
        this.good = good.id
        this.baseUnit = baseUnit
    }
}

class DocumentAndRows() {
    @Embedded
    var document: DctDocumentHeader = DctDocumentHeader()

    @Relation(
        parentColumn = "id",
        entityColumn = "document",
        entity = DctDocumentRow::class
    )
    var rows: MutableList<DctDocumentRow> = ArrayList()

    constructor(viewDocument: ViewDocument): this() {
        document = viewDocument.header
        rows.clear()
        viewDocument.rows.forEach { rows.add(DctDocumentRow(it, document.id)) }
    }
}

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
        this.good = Good()
        this.unit = Unit(barcode, good, true)
    }
}

data class TestData(
    val id: String,
    val number: Int,
    val string: String,
)
