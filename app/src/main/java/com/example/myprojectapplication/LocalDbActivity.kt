package com.example.myprojectapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.database.InfoAudio
import com.example.myprojectapplication.database.InfoAudioApp
import com.example.myprojectapplication.database.UploadData
import com.example.myprojectapplication.database.UploadDataAdapter
import com.example.myprojectapplication.database.UploadDataApp
import com.example.myprojectapplication.database.UploadDataRepository
import com.example.myprojectapplication.database.UploadDataViewModel
import com.example.myprojectapplication.database.UploadDataViewModelFactory
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

class LocalDbActivity : AppCompatActivity() {

    private lateinit var username: String
    private lateinit var clientToken: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnDeleteAll: Button
    private lateinit var adapter: UploadDataAdapter
    private val uploadDataViewModel: UploadDataViewModel by viewModels {
        val dao = UploadDataApp.getDatabase(application).uploadDataDao()
        val repository = UploadDataRepository(dao)
        UploadDataViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_db)
        UtilNetwork.checkConnection(this)

        username = intent.getStringExtra("username")!!
        clientToken = intent.getStringExtra("clientToken")!!

        recyclerView = findViewById(R.id.recyclerView)
        btnDeleteAll = findViewById(R.id.btnDeleteAll)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UploadDataAdapter(emptyList())
        recyclerView.adapter = adapter

        uploadDataViewModel.allUploadData.observe(this, Observer { dataList ->
            adapter.setData(dataList)
        })

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnDeleteAll.setOnClickListener {
            deleteAllFromDb()
        }

        val btnUpload: Button = findViewById(R.id.btnUpload)
        btnUpload.setOnClickListener {
            uploadAllFiles(clientToken)
        }
    }

    private fun deleteAllFromDb() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to delete all files from this Db? You won't be able to upload them.")
            .setPositiveButton("Yes") { dialog, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val db = UploadDataApp.getDatabase(this@LocalDbActivity)
                        db.uploadDataDao().deleteAll()
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LocalDbActivity, "All elements deleted from DB", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun uploadAllFiles(token: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to upload all files from this Db?")
            .setPositiveButton("Yes") { dialog, _ ->
                lifecycleScope.launch {
                    val db = UploadDataApp.getDatabase(this@LocalDbActivity)
                    val uploadDataList = withContext(Dispatchers.IO) {
                        db.uploadDataDao().getAllUploadDataBlocking()
                    }

                    uploadDataList.forEach { uploadData ->
                        uploadFile(token, uploadData)
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun uploadFile(token: String, uploadData: UploadData) {
        val recordingFilePath = "${filesDir.absolutePath}/${uploadData.username}_${uploadData.latitude}_${uploadData.longitude}.mp3"
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
                            insertInfoAudio(responseUpload, recordingFilePath, uploadData.longitude, uploadData.latitude)
                            deleteUploadDataFromDb(uploadData)
                            Toast.makeText(this@LocalDbActivity, "File correctly uploaded", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        handleUploadError(response.code())
                    }
                }

                override fun onFailure(call: Call<ResponseUpload>, t: Throwable) {
                    Toast.makeText(this@LocalDbActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@LocalDbActivity, "File does not exist: $recordingFilePath", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleUploadError(code: Int) {
        val responseText = when (code) {
            401 -> {
                this@LocalDbActivity.let { utilLogin.forceLogin(it) }
                "User is not authenticated."
            }
            413 -> "File is too big."
            415 -> "File is not a supported audio file."
            else -> "Unknown error."
        }
        Toast.makeText(this@LocalDbActivity, responseText, Toast.LENGTH_SHORT).show()
    }

    private fun deleteUploadDataFromDb(uploadData: UploadData) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val db = UploadDataApp.getDatabase(this@LocalDbActivity)
                db.uploadDataDao().delete(uploadData.username, uploadData.latitude, uploadData.longitude)
            }
        }
    }

    private fun insertInfoAudio(responseUpload: ResponseUpload, recordingFilePath: String, longitude: Double, latitude: Double) {

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

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val db = InfoAudioApp.getDatabase(this@LocalDbActivity)
                db.infoAudioDao().insert(infoAudio)
            }
        }
    }


}