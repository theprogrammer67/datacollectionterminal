package ru.rarus.datacollectionterminal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

class DctFocument() {
    val header = DctDocumentHeader()
    val rows: MutableList<DctDocumentRow> =  ArrayList()
}

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
    }
}

data class TestData(
    val id: String,
    val number: Int,
    val string: String,
)
