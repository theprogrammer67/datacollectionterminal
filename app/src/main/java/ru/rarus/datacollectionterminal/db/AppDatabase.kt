package ru.rarus.datacollectionterminal.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.rarus.datacollectionterminal.db.entities.DocumentHeader
import ru.rarus.datacollectionterminal.db.entities.DocumentRow
import ru.rarus.datacollectionterminal.db.entities.Good
import ru.rarus.datacollectionterminal.db.entities.Unit

@Database(
    entities = [DocumentHeader::class, DocumentRow::class, Good::class, Unit::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): DctDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: Room.databaseBuilder(context, AppDatabase::class.java, "database.db")
                .fallbackToDestructiveMigration()
                .build().also { instance = it }
        }
    }
}