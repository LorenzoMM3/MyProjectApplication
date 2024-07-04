package com.example.myprojectapplication

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

object UtilNetwork {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun checkConnection(context: Context){
        if (!UtilNetwork.isNetworkAvailable(context)) {
            Toast.makeText(context, "No internet connection. Redirecting to InfoAudio Database.", Toast.LENGTH_LONG).show()
            val intent = Intent(context, InfoAudioActivity::class.java)
            context.startActivity(intent)
        }
    }
}
