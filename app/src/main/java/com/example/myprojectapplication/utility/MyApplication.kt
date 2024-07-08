package com.example.myprojectapplication.utility

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MyApplication: Application() {

    private val CHANNEL_ID = "WIFI_STATUS_CHANNEL"
    private val CHANNEL_NAME = "WiFi Status"
    private val CHANNEL_DESCRIPTION = "Notifications for WiFi status changes"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}