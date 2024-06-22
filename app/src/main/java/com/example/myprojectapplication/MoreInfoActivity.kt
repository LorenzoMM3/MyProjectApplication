package com.example.myprojectapplication

import ApiService
import ResponseMoreInfo
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoreInfoActivity : AppCompatActivity() {

    private lateinit var infoContainer: LinearLayout
    private var uploadId: Int = -1
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)

        infoContainer = findViewById(R.id.infoContainer)
        val btnBack1mi: Button = findViewById(R.id.btnBack1mi)

        uploadId = intent.getIntExtra("uploadId", -1)
        token = intent.getStringExtra("token")

        if (uploadId != -1 && token != null) {
            fetchMoreInfo(uploadId, token!!)
        } else {
            Toast.makeText(this, "Invalid upload ID or token", Toast.LENGTH_SHORT).show()
        }

        btnBack1mi.setOnClickListener {
            onBackPressed()
        }

    }

    private fun fetchMoreInfo(uploadId: Int, token: String) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val authHeader = "Bearer $token"

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.seeMoreInfo(authHeader, uploadId).execute()
                }

                if (response.isSuccessful) {
                    response.body()?.let { moreInfo ->
                        displayMoreInfo(moreInfo)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@MoreInfoActivity, "Failed to fetch info: ${response.code()} - $errorBody", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MoreInfoActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayMoreInfo(info: ResponseMoreInfo) {
        val infoTextView = TextView(this).apply {
            text = """
                ID: ${info.id}
                Longitude: ${info.longitude}
                Latitude: ${info.latitude}
                Creator ID: ${info.creator_id}
                Creator Username: ${info.creator_username}
                BPM: ${info.tags.bpm}
                Danceability: ${info.tags.danceability}
                Loudness: ${info.tags.loudness}
                Mood: ${info.tags.mood}
                Genre: ${info.tags.genre}
                Instrument: ${info.tags.instrument}
            """.trimIndent()
            textSize = 16f
            setPadding(0, 16, 0, 16)
        }

        infoContainer.addView(infoTextView)
    }
}
