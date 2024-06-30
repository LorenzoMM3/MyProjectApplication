package com.example.myprojectapplication

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

private const val ARG_TOKEN = "token"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class RecordFragment : Fragment() {
    private var token: String? = null
    private var isRecording = false
    private lateinit var btnListener: Button
    private lateinit var btnDeleteAudio: Button
    private lateinit var uploadsContainer2: LinearLayout
    private var recordingFilePath: String = ""
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var recorder: MediaRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(ARG_TOKEN)
        }
        recordingFilePath = "${requireActivity().externalCacheDir?.absolutePath}/audiorecordtest.mp3"
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record, container, false)
        btnListener = view.findViewById(R.id.btnListener)
        btnDeleteAudio = view.findViewById(R.id.btnDeleteAudio)
        uploadsContainer2 = view.findViewById(R.id.uploadsContainer2)

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

        return view
    }

    private fun startRecording() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
            return
        }

        addResponseToContainer("I am Listening... Click again to stop the recording")

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
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        btnListener.text = "Start Listening"
        isRecording = false
        getLastKnownLocationAndUpload()
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

    private fun getLastKnownLocationAndUpload() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    token?.let { uploadFile(it) }
                } ?: addResponseToContainer("Location is not available.")
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }

    private fun uploadFile(token: String) {
        val file = File(recordingFilePath)
        val requestFile = RequestBody.create(MediaType.parse("audio/mpeg"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val apiService = ApiClient.instance.create(ApiService::class.java)
        if (latitude != null && longitude != null) {
            val call = apiService.uploadFile("Bearer $token", longitude!!, latitude!!, body)
            call.enqueue(object : Callback<ResponseUpload> {
                override fun onResponse(call: Call<ResponseUpload>, response: Response<ResponseUpload>) {
                    val responseText = when (response.code()) {
                        200 -> "File correctly uploaded."
                        401 -> {
                            context?.let { utilLogin.forceLogin(it) }
                            "User is not authenticated."
                        }
                        413 -> "File is too big."
                        415 -> "File is not a supported audio file."
                        else -> "Unknown error."
                    }
                    addResponseToContainer(responseText)
                }

                override fun onFailure(call: Call<ResponseUpload>, t: Throwable) {
                    addResponseToContainer("Upload failed: ${t.message}")
                }
            })
        } else {
            addResponseToContainer("Location is not available.")
        }
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
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastKnownLocationAndUpload()
                } else {
                    Toast.makeText(context, "Permission to access location denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(token: String) =
            RecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TOKEN, token)
                }
            }
    }
}
