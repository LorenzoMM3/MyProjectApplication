package com.example.myprojectapplication.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myprojectapplication.ApiClient
import com.example.myprojectapplication.ApiService
import com.example.myprojectapplication.MainActivity
import com.example.myprojectapplication.R
import com.example.myprojectapplication.ResponseUpload
import com.example.myprojectapplication.database.AppDatabase
import com.example.myprojectapplication.database.InfoAudio
import com.example.myprojectapplication.database.UploadData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class NetworkChangeReceiver(private val token: String) : BroadcastReceiver() {

    private var lastTime = 0L
    private var interval = 3000L

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime < interval){
                return
            }
            lastTime = currentTime
            if (isWifiConnected(context)) {
                CoroutineScope(Dispatchers.IO).launch {
                    checkUploadDataAndNotify(context)
                }
            }
        }
    }

    private fun isWifiConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private suspend fun checkUploadDataAndNotify(context: Context) {
        val db = AppDatabase.getDatabase(context)
        val uploadDataDao = db.uploadDataDao()

        val uploadDataExists = withContext(Dispatchers.IO) {
            uploadDataDao.getAllUploadDataBlocking().isNotEmpty()
        }
        // AGGIUNGERE CONTROLLO DEL TOKEN
        if (uploadDataExists) {
            withContext(Dispatchers.IO) {
                uploadFromDb(context)
            }
        }
    }

    private fun uploadFromDb(context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val uploadDataList = db.uploadDataDao().getAllUploadDataBlocking()
            uploadDataList.forEach { uploadData ->
                uploadFile(context, uploadData)
            }
        }
        sendNotification(context)
    }

    private fun uploadFile(context: Context, uploadData: UploadData) {
        val recordingFilePath = "${context.filesDir.absolutePath}/${uploadData.username}_${uploadData.latitude}_${uploadData.longitude}.mp3"
        val file = File(recordingFilePath)
        if (file.exists()) {
            val requestFile = RequestBody.create(MediaType.parse("audio/mpeg"), file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val apiService = ApiClient.instance.create(ApiService::class.java)
            val call = apiService.uploadFile("Bearer $token", uploadData.longitude, uploadData.latitude, body)
            call.enqueue(object : Callback<ResponseUpload> {
                override fun onResponse(call: Call<ResponseUpload>, response: Response<ResponseUpload>) {
                    if (response.isSuccessful) {
                        val responseUpload = response.body()
                        if (responseUpload != null) {
                            insertInfoAudio(context, responseUpload, recordingFilePath, uploadData.longitude, uploadData.latitude)
                            deleteUploadDataFromDb(context, uploadData)
                        }
                    } else {
                        Log.e("Errore Upload Background","Errore: ${response.errorBody()!!.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseUpload>, t: Throwable) {
                    Log.e("Errore Upload Background","Errore: Api Call Failure")
                }
            })
        } else {
            Log.e("Errore Upload Background","Errore: File non esistente")
        }
    }

    private fun deleteUploadDataFromDb(context: Context, uploadData: UploadData) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            db.uploadDataDao().delete(uploadData.username, uploadData.latitude, uploadData.longitude)
        }
    }

    private fun insertInfoAudio(context: Context, responseUpload: ResponseUpload, recordingFilePath: String, longitude: Double, latitude: Double) {

        fun formatPercentage(value: Double): String {
            return String.format("%.2f%%", value * 100)
        }

        val stringMood = responseUpload.mood?.entries?.sortedByDescending { it.value }?.take(5)?.joinToString { (key, value) ->
            "$key: ${formatPercentage(value)}"
        }.orEmpty()

        val stringGenre = responseUpload.genre?.entries?.sortedByDescending { it.value }?.take(5)?.joinToString { (key, value) ->
            "$key: ${formatPercentage(value)}"
        }.orEmpty()

        val stringInstrument = responseUpload.instrument?.entries?.sortedByDescending { it.value }?.take(5)?.joinToString { (key, value) ->
            "$key: ${formatPercentage(value)}"
        }.orEmpty()

        val infoAudio = InfoAudio(
            longitude = longitude,
            latitude = latitude,
            bpm = responseUpload.bpm!!,
            danceability = responseUpload.danceability!!,
            loudness = responseUpload.loudness!!,
            mood = stringMood,
            genre = stringGenre,
            instrument = stringInstrument,
            audioFilePath = recordingFilePath
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            db.infoAudioDao().insert(infoAudio)
        }
    }

    private fun sendNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationId = 1
        val channelId = "WIFI_STATUS_CHANNEL"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_location_file)
            .setContentTitle("Network Change")
            .setContentText("WiFi is now connected. Your audios have been uploaded.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder)
    }

}
