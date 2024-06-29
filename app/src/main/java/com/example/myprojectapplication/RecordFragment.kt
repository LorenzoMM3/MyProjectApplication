package com.example.myprojectapplication

import ApiService
import ResponseUpload
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
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
    private lateinit var uploadsContainer2: LinearLayout
    private var recordingFilePath: String = ""
    private var recorder: MediaRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(ARG_TOKEN)
        }
        recordingFilePath = "${requireActivity().externalCacheDir?.absolutePath}/audiorecordtest.mp3"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record, container, false)
        btnListener = view.findViewById(R.id.btnListener)
        uploadsContainer2 = view.findViewById(R.id.uploadsContainer2)

        btnListener.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        return view
    }

    private fun startRecording() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
            return
        }

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
        token?.let { uploadFile(it) }
    }

    private fun uploadFile(token: String) {
        val file = File(recordingFilePath)
        val requestFile = RequestBody.create(MediaType.parse("audio/mpeg"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.uploadFile("Bearer $token", 43.0, 44.0, body)
        call.enqueue(object : Callback<ResponseUpload> {
            override fun onResponse(call: Call<ResponseUpload>, response: Response<ResponseUpload>) {
                val responseText = when (response.code()) {
                    200 -> "File correctly uploaded."
                    401 -> "User is not authenticated."
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
    }

    private fun addResponseToContainer(message: String) {
        val textView = TextView(context).apply {
            text = message
            textSize = 16f
            setPadding(0, 16, 0, 16)
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
        fun newInstance(token: String) =
            RecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TOKEN, token)
                }
            }
    }
}
