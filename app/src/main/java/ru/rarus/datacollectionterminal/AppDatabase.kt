package ru.rarus.datacollectionterminal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DctDocumentHeader::class, DctDocumentRow::class, ], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dctDao(): DctDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: Room.databaseBuilder(context, AppDatabase::class.java, "database.db").
            build().also { instance = it }
        }
    }
}