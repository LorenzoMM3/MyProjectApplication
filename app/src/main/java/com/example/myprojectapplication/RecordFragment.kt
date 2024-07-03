package com.example.myprojectapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.myprojectapplication.database.UploadDataApp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import androidx.lifecycle.lifecycleScope
import com.example.myprojectapplication.database.UploadData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_TOKEN = "token"
private const val ARG_USERNAME = "username"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class RecordFragment : Fragment() {
    private var token: String? = null
    private var username: String? = null
    private var isRecording = false
    private lateinit var btnListener: Button
    private lateinit var btnDeleteAudio: Button
    private lateinit var btnPlayAudio: Button
    private lateinit var btnPauseAudio: Button
    private lateinit var btnStopAudio: Button
    private lateinit var uploadsContainer2: LinearLayout
    private var recordingFilePath: String = ""
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var recorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var database: UploadDataApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(ARG_TOKEN)
            username = it.getString(ARG_USERNAME)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        database = UploadDataApp.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record, container, false)
        btnListener = view.findViewById(R.id.btnListener)
        btnDeleteAudio = view.findViewById(R.id.btnDeleteAudio)
        btnPlayAudio = view.findViewById(R.id.btnPlayAudio)
        btnPauseAudio = view.findViewById(R.id.btnPauseAudio)
        btnStopAudio = view.findViewById(R.id.btnStopAudio)
        uploadsContainer2 = view.findViewById(R.id.uploadsContainer2)
        getLastKnownLocation()

        btnListener.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        btnDeleteAudio.setOnClickListener {
            if (isRecording) {
                deleteRecording()
            } else {
                addResponseToContainer("No recording to delete")
            }
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

        return view
    }

    private fun startRecording() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
            return
        }

        if (latitude != null && longitude != null && username != null) {
            addResponseToContainer("I am Listening... - latitude: $latitude - longitude: $longitude - username: $username - ")
            recordingFilePath = "${requireActivity().filesDir.absolutePath}/${username}_${latitude}_${longitude}.mp3"
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(recordingFilePath)

                try {
                    prepare()
                    start()
                    btnListener.text = "Stop Listening"
                    isRecording = true
                } catch (e: IOException) {
                    Log.e("RecordFragment", "prepare() failed: ${e.message}")
                }
            }
        } else {
            addResponseToContainer("Error: impossible to continue - latitude: $latitude - longitude: $longitude - username: $username - ")
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        btnListener.text = "Start Listening"
        isRecording = false
        uploadFile(token!!)
    }

    private fun deleteRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        btnListener.text = "Start Listening"
        isRecording = false

        val file = File(recordingFilePath)
        if (file.exists()) {
            file.delete()
            addResponseToContainer("Audio Deleted")
        } else {
            addResponseToContainer("No recording to delete")
        }
    }

    private fun playRecording() {
        val file = File(recordingFilePath)
        if (file.exists()) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(recordingFilePath)
                    prepareAsync()
                    setOnPreparedListener {
                        start()
                        addResponseToContainer("Playing Audio")
                    }
                }
            } else {
                mediaPlayer?.start()
                addResponseToContainer("Resuming Audio")
            }
        } else {
            addResponseToContainer("No recording to play")
        }
    }

    private fun pauseRecording() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                addResponseToContainer("Audio paused")
            } else {
                addResponseToContainer("No audio is currently playing")
            }
        }
    }

    private fun insertDb(uploadData: UploadData){
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                database.uploadDataDao().insert(uploadData)
            }
        }
    }

    private fun stopPlayingRecording() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                mediaPlayer = null
                addResponseToContainer("Audio stopped")
            } else {
                addResponseToContainer("No audio is currently playing")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude

                } ?: addResponseToContainer("Location is not available.")
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun uploadFile(token: String) {
        val file = File(recordingFilePath)
        val requestFile = RequestBody.create(MediaType.parse("audio/mpeg"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.uploadFile("Bearer $token", longitude!!, latitude!!, body)
        val uploadData = UploadData(username!!, latitude!!, longitude!!)
            call.enqueue(object : Callback<ResponseUpload> {
                override fun onResponse(call: Call<ResponseUpload>, response: Response<ResponseUpload>) {
                    val responseText = when (response.code()) {
                        200 -> {
                            "File correctly uploaded. in $recordingFilePath"
                        }
                        401 -> {
                            context?.let { utilLogin.forceLogin(it) }
                            "User is not authenticated."
                        }
                        413 -> "File is too big."
                        415 -> "File is not a supported audio file."
                        else -> "Unknown error."
                    }
                    if(response.code()==200){
                        insertDb(uploadData)
                        Toast.makeText(context, "Dati Inseriti nel DB", Toast.LENGTH_SHORT).show()
                    }
                    addResponseToContainer(responseText)
                }

                override fun onFailure(call: Call<ResponseUpload>, t: Throwable) {
                    addResponseToContainer("Upload failed: ${t.message}")
                }
            })
    }

    private fun addResponseToContainer(message: String) {
        uploadsContainer2.removeAllViews()
        val textView = TextView(context).apply {
            text = message
            textSize = 18f
            setPadding(0, 16, 0, 16)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }
        uploadsContainer2.addView(textView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording()
                } else {
                    Toast.makeText(context, "Permission to record audio denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(token: String, username: String) =
            RecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TOKEN, token)
                    putString(ARG_USERNAME, username)
                }
            }
    }
}
