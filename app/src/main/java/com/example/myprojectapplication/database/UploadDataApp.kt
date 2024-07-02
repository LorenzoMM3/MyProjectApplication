package com.example.myprojectapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UploadData::class], version = 1, exportSchema = false)
abstract class UploadDataApp : RoomDatabase() {

    abstract fun uploadDataDao(): UploadDataDao

    companion object {
        @Volatile
        private var INSTANCE: UploadDataApp? = null

        fun getDatabase(context: Context): UploadDataApp {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UploadDataApp::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
