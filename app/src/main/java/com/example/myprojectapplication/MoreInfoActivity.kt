package com.example.myprojectapplication

import ApiService
import ResponseMoreInfo
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
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
        fun formatPercentage(value: Double): String {
            return String.format("%.2f%%", value * 100)
        }
        val top5Mood = info.tags.mood.entries.sortedByDescending { it.value }.take(5)
        val top5Genre = info.tags.genre.entries.sortedByDescending { it.value }.take(5)
        val top5Instrument = info.tags.instrument.entries.sortedByDescending { it.value }.take(5)
        val infoText = buildString {
            appendLine("<b>ID:</b> ${info.id}<br>")
            appendLine("<b>Longitude:</b> ${info.longitude}<br>")
            appendLine("<b>Latitude:</b> ${info.latitude}<br>")
            appendLine("<b>Creator ID:</b> ${info.creator_id}<br>")
            appendLine("<b>Creator Username:</b> ${info.creator_username}<br>")
            appendLine("<b>BPM:</b> ${info.tags.bpm}<br>")
            appendLine("<b>Danceability:</b> ${info.tags.danceability}<br>")
            appendLine("<b>Loudness:</b> ${info.tags.loudness}<br>")
            appendLine("<b>Top 5 Mood:</b><br>")
            top5Mood.forEach { (key, value) ->
                appendLine("$key: ${formatPercentage(value)}<br>")
            }
            appendLine("<b>Top 5 Genre:</b><br>")
            top5Genre.forEach { (key, value) ->
                appendLine("$key: ${formatPercentage(value)}<br>")
            }
            appendLine("<b>Top 5 Instrument:</b><br>")
            top5Instrument.forEach { (key, value) ->
                appendLine("$key: ${formatPercentage(value)}<br>")
            }
        }

        val infoTextView = TextView(this).apply {
            text = Html.fromHtml(infoText, Html.FROM_HTML_MODE_COMPACT)
            textSize = 18f
            setPadding(0, 25, 0, 25)
        }
        infoContainer.addView(infoTextView)
    }


}
