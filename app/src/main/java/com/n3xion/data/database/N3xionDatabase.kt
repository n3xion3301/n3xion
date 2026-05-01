package com.n3xion.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.n3xion.data.database.entities.Contact
import com.n3xion.data.database.entities.Message

@Database(
    entities = [Message::class, Contact::class],
    version = 1,
    exportSchema = false
)
abstract class N3xionDatabase : RoomDatabase() {
    
    abstract fun messageDao(): MessageDao
    abstract fun contactDao(): ContactDao
    
    companion object {
        @Volatile
        private var INSTANCE: N3xionDatabase? = null
        
        fun getDatabase(context: Context): N3xionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    N3xionDatabase::class.java,
                    "n3xion_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
