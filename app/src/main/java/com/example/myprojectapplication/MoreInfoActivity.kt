package com.example.myprojectapplication

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoreInfoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MoreInfoAdapter
    private var uploadId: Int = -1
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

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

    private fun displayMoreInfo(info: ResponseMoreInfo) {
        fun formatPercentage(value: Double): String {
            return String.format("%.2f%%", value * 100)
        }

        val infoList = mutableListOf<String>().apply {
            add("Audio ID: ${info.id}")
            add("Longitude: ${info.longitude}")
            add("Latitude: ${info.latitude}")
            add("Creator ID: ${info.creator_id}")
            add("Creator Username: ${info.creator_username}")
            add("BPM: ${info.tags.bpm}")
            add("Danceability: ${info.tags.danceability}")
            add("Loudness: ${info.tags.loudness}")

            add("Top 5 Mood:")
            info.tags.mood.entries.sortedByDescending { it.value }.take(5).forEach { (key, value) ->
                add("$key: ${formatPercentage(value)}")
            }

            add("Top 5 Genre:")
            info.tags.genre.entries.sortedByDescending { it.value }.take(5).forEach { (key, value) ->
                add("$key: ${formatPercentage(value)}")
            }

            add("Top 5 Instrument:")
            info.tags.instrument.entries.sortedByDescending { it.value }.take(5).forEach { (key, value) ->
                add("$key: ${formatPercentage(value)}")
            }
        }

        adapter = MoreInfoAdapter(infoList)
        recyclerView.adapter = adapter
    }
}