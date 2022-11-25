package ru.rarus.datacollectionterminal.db.dao

import androidx.room.*
import ru.rarus.datacollectionterminal.db.entities.*
import java.util.*

@Dao
abstract class DocumentDao {

    @Query("SELECT * FROM document ORDER BY date ASC")
    abstract fun getAllDocuments(): List<DocumentHeader>

    @Query("SELECT * FROM document WHERE id = :id")
    abstract fun getDocument(id: String): DocumentHeader?

    @Query(
        """
        SELECT document_row.*, unit.name as unitName, unit.price as unitPrice, good.name as goodName, good.id as good
        FROM document_row
        LEFT JOIN unit on(document_row.unit = unit.barcode)
        LEFT JOIN good ON (unit.good = good.id)
        WHERE document_row.document = :id
        """
    )
    abstract fun getViewDocumentRows(id: String): List<ViewDocumentRow>

    @Query("DELETE FROM document_row WHERE document = :id")
    abstract fun deleteDocumentRows(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDocument(document: DocumentHeader): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDocuments(documents: List<DocumentHeader>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertDocumentRows(items: List<DocumentRow>): List<Long>

    @Query("DELETE FROM document")
    abstract fun deleteAllDocuments()

    @Query("DELETE FROM document WHERE id = :id")
    abstract fun deleteDocument(id: String)

    @Query("DELETE FROM document  WHERE id IN (:id)")
    abstract fun deleteDocuments(id: List<String>)
}
