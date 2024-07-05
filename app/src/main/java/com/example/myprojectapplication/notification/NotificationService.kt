package com.example.myprojectapplication.notification

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.IBinder

class NotificationService: Service() {

    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    override fun onCreate() {
        super.onCreate()
        networkChangeReceiver = NetworkChangeReceiver()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}