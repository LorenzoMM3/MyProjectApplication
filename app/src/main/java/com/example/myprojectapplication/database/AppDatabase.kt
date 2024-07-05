package com.example.myprojectapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [InfoAudio::class, UploadData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun infoAudioDao(): InfoAudioDao
    abstract fun uploadDataDao(): UploadDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
