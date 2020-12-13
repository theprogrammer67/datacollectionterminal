package ru.rarus.datacollectionterminal

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DctDao {
    @Query("SELECT * FROM document ORDER BY date ASC")
    fun getDocuments(): LiveData<List<DctDocumentHeader>>

    @Transaction
    @Query("SELECT * from document")
    fun getDocumentAndRows(): LiveData<List<DocumentAndRows>>

    @Query("SELECT * FROM document WHERE id = :id")
    fun getDocument(id: String): LiveData<DctDocumentHeader>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDocument(documentHeader: DctDocumentHeader)

    @Query("DELETE FROM document WHERE id = :id")
    suspend fun deleteDocument(id: String)

    @Query("DELETE FROM document")
    suspend fun deleteDocuments()

    @Query("SELECT * FROM unit WHERE barcode = :barcode")
    fun getUnitByBarcode(barcode: String): LiveData<Unit>

//    @Query("SELECT * FROM unit WHERE barcode = :barcode")
//    suspend fun getDocRowByCarcode(barcode: String)
}