package ru.rarus.datacollectionterminal

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DctDao {
    @Query("SELECT * FROM document ORDER BY date ASC")
    fun getDocuments(): LiveData<List<DctDocument>>

    @Query("SELECT * FROM document WHERE id = :id")
    fun getDocument(id: String): LiveData<DctDocument>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDocument(document: DctDocument)

    @Query("DELETE FROM document WHERE id = :id")
    suspend fun deleteDocument(id: String)

    @Query("DELETE FROM document")
    suspend fun deleteDocuments()
}