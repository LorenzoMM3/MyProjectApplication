package com.example.myprojectapplication

import android.media.MediaPlayer
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
import java.io.File
import java.io.IOException

class MoreInfoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MoreInfoAdapter
    private var uploadId: Int = -1
    private var token: String? = null
    private var audioFilePath: String? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnBack1mi: Button = findViewById(R.id.btnBack1mi)
        val btnPlayAudio: Button = findViewById(R.id.btnPlayAudio)
        val btnPauseAudio: Button = findViewById(R.id.btnPauseAudio)
        val btnStopAudio: Button = findViewById(R.id.btnStopAudio)

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

        btnPlayAudio.setOnClickListener {
            playRecording()
        }

        btnPauseAudio.setOnClickListener {
            pauseRecording()
        }

        btnStopAudio.setOnClickListener {
            stopPlayingRecording()
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
                    Toast.makeText(this@MoreInfoActivity, "$audioFilePath", Toast.LENGTH_LONG).show()
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

            audioFilePath = "${filesDir.absolutePath}/${info.creator_username}_${info.latitude}_${info.longitude}.mp3"
            add("Audio File Path: $audioFilePath")
        }

        adapter = MoreInfoAdapter(infoList)
        recyclerView.adapter = adapter
    }

    private fun playRecording() {
        val file = File(audioFilePath!!)
        if (file.exists()) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioFilePath)
                    prepareAsync()
                    setOnPreparedListener {
                        start()
                        Toast.makeText(this@MoreInfoActivity, "Playing Audio", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                mediaPlayer?.start()
                Toast.makeText(this@MoreInfoActivity, "Resuming Audio", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@MoreInfoActivity, "No Recording to Play", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pauseRecording() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                Toast.makeText(this@MoreInfoActivity, "Audio Paused", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MoreInfoActivity, "No Audio currently Playing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopPlayingRecording() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                mediaPlayer = null
                Toast.makeText(this@MoreInfoActivity, "Audio Stopped", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MoreInfoActivity, "No Audio is currrently Playing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
