package com.example.myprojectapplication.notification

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.IBinder

class NotificationService: Service() {

    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private var clientToken: String = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null){
            clientToken = intent.getStringExtra("clientToken")!!
            networkChangeReceiver = NetworkChangeReceiver(clientToken)
            val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(networkChangeReceiver, intentFilter)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}