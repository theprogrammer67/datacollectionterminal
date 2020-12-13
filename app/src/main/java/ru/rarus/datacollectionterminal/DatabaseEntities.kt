package ru.rarus.datacollectionterminal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
class Unit(var good: String) {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name: String = "шт"
    var barcode: String = ""
    var conversionFactor: Double = 1.0
    var price: Double = 0.0
    var baseinUnit: Boolean = false
}

data class TestData(
    val id: String,
    val number: Int,
    val string: String,
)
