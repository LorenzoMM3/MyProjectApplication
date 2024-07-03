package com.example.myprojectapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [InfoAudio::class], version = 1)
abstract class InfoAudioApp : RoomDatabase() {

    abstract fun infoAudioDao(): InfoAudioDao

    companion object {
        @Volatile
        private var INSTANCE: InfoAudioApp? = null

        fun getDatabase(context: Context): InfoAudioApp {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InfoAudioApp::class.java,
                    "info_audio_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
