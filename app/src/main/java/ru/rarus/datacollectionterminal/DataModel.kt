package ru.rarus.datacollectionterminal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "document")
data class DctDocument(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val number: String,
    val date: Long,
    val source: Int,
    val state: Int

)

@Entity(
    tableName = "document_row", foreignKeys = [ForeignKey(
        entity = DctDocument::class,
        parentColumns = ["id"],
        childColumns = ["document"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class DctDocumentRow(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val document: String,
    val unit: String,
    val addBarcode: String,
    val quantityDoc: Double,
    val quantityActual: Double,
    val difference: Double
)
