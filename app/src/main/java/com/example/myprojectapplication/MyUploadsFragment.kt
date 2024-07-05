package com.example.myprojectapplication

import android.widget.Toast
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myprojectapplication.database.AppDatabase
import com.example.myprojectapplication.database.UploadDataRepository
import com.example.myprojectapplication.database.UploadDataViewModel
import com.example.myprojectapplication.database.UploadDataViewModelFactory
import com.example.myprojectapplication.repository.InfoAudioRepository
import com.example.myprojectapplication.utilLogin.forceLogin
import com.example.myprojectapplication.viewmodel.InfoAudioViewModel
import com.example.myprojectapplication.viewmodel.InfoAudioViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_TOKEN = "token"
private const val ARG_USERNAME = "username"

class MyUploadsFragment : Fragment() {
    private var token: String? = null
    private var username: String? = null
    private lateinit var uploadsContainer: LinearLayout
    private lateinit var uploadsContainer2: LinearLayout
    private lateinit var buttonLocalDb: Button
    private lateinit var btnInfoAudio: Button

    private val uploadDataViewModel: UploadDataViewModel by viewModels {
        val dao = AppDatabase.getDatabase(requireContext()).uploadDataDao()
        val repository = UploadDataRepository(dao)
        UploadDataViewModelFactory(repository)
    }

    private val infoAudioViewModel: InfoAudioViewModel by viewModels {
        val dao = AppDatabase.getDatabase(requireContext()).infoAudioDao()
        val repository = InfoAudioRepository(dao)
        InfoAudioViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(ARG_TOKEN)
            username = it.getString(ARG_USERNAME)
        }
        UtilNetwork.checkConnection(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_uploads, container, false)
        uploadsContainer = view.findViewById(R.id.uploadsContainer)
        uploadsContainer2 = view.findViewById(R.id.uploadsContainer2)
        buttonLocalDb = view.findViewById(R.id.buttonLocalDb)
        btnInfoAudio = view.findViewById(R.id.buttonLocalDb2)

        token?.let { fetchMyUploads(it) }
        token?.let { fetchAllUploads(it) }

        buttonLocalDb.setOnClickListener {
            val intent = Intent(context, LocalDbActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("clientToken", token)
            startActivity(intent)
        }

        btnInfoAudio.setOnClickListener {
            val intent = Intent(context, InfoAudioActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun fetchMyUploads(token: String) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.seeMyUploads("Bearer $token")

        call.enqueue(object : Callback<List<ResponseMyUploads>> {
            override fun onResponse(call: Call<List<ResponseMyUploads>>, response: Response<List<ResponseMyUploads>>) {
                if (response.isSuccessful) {
                    val uploads = response.body()
                    uploads?.let {
                        displayMyUploads(it)
                    }
                } else {
                    val errorMessage: String = when (response.code()) {
                        401 -> {
                            context?.let { forceLogin(it) }
                            "User is not authenticated"
                        }
                        else -> "Fetch My Error: ${response.errorBody()?.string()}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<ResponseMyUploads>>, t: Throwable) {
                Toast.makeText(context, "FetchMy Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAllUploads(token: String) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.seeAllUploads("Bearer $token")

        call.enqueue(object : Callback<List<ResponseAllUploads>> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<List<ResponseAllUploads>>, response: Response<List<ResponseAllUploads>>) {
                if (response.isSuccessful) {
                    val uploads = response.body()
                    uploads?.let {
                        displayAllUploads(it)
                    }
                } else {
                    val errorMessage: String = when (response.code()) {
                        401 -> {
                            context?.let { forceLogin(it) }
                            "User is not authenticated"
                        }
                        else -> "Fetch All Error: ${response.errorBody()?.string()}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<ResponseAllUploads>>, t: Throwable) {
                Toast.makeText(context, "FetchAll Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteFile(token: String, id: Int, latitude: Double, longitude: Double) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.deleteFile("Bearer $token", id)

        call.enqueue(object : Callback<ResponseDeleteFile> {
            override fun onResponse(call: Call<ResponseDeleteFile>, response: Response<ResponseDeleteFile>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Song deleted successfully!", Toast.LENGTH_SHORT).show()
                    if(response.code()==200){
                        deleteFromInfoAudio(latitude, longitude)
                        Toast.makeText(context, "Deleted from Info Audio", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage: String = when (response.code()) {
                        401 -> {
                            context?.let { forceLogin(it) }
                            "User is not authenticated"
                        }
                        404 -> "Audio not found"
                        400 -> "You are not authorized to delete this audio"
                        else -> "Delete Error: ${response.errorBody()?.string()}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseDeleteFile>, t: Throwable) {
                Toast.makeText(context, "Delete Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showUpload(token: String,  id: Int) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.showFile("Bearer $token", id)

        call.enqueue(object : Callback<ResponseShowFile> {
            override fun onResponse(call: Call<ResponseShowFile>, response: Response<ResponseShowFile>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Audio Shown", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage: String = when (response.code()) {
                        401 -> {
                            context?.let { forceLogin(it) }
                            "User is not authenticated"
                        }
                        404 -> "Audio not found"
                        400 -> "You are not authorized to do this audio"
                        else -> "Delete Error: ${response.errorBody()?.string()}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseShowFile>, t: Throwable) {
                Toast.makeText(context, "Show Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun hideUpload(token: String,  id: Int) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.hideFile("Bearer $token", id)

        call.enqueue(object : Callback<ResponseHideFile> {
            override fun onResponse(call: Call<ResponseHideFile>, response: Response<ResponseHideFile>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Audio Hidden", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage: String = when (response.code()) {
                        401 -> {
                            context?.let { forceLogin(it) }
                            "User is not authenticated"
                        }
                        404 -> "Audio not found"
                        400 -> "You are not authorized to do this audio"
                        else -> "Delete Error: ${response.errorBody()?.string()}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseHideFile>, t: Throwable) {
                Toast.makeText(context, "Hide Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun displayMyUploads(uploads: List<ResponseMyUploads>) {
        uploadsContainer.removeAllViews()
        uploads.forEach { upload ->
            val textView = TextView(context).apply {
                text = """
                    ID: ${upload.id}
                    Longitude: ${upload.longitude}
                    Latitude: ${upload.latitude}
                    Hidden: ${upload.hidden}
                    Uploaded: ${upload.uploaded}
                    Possible Actions: 
                """.trimIndent()
                setTextColor(resources.getColor(R.color.black, null))
                textSize = 16f
                setPadding(0, 16, 0, 16)
            }

            val moreInfoButton = Button(context).apply {
                text = "More Info"
                setOnClickListener {
                    val intent = Intent(context, MoreInfoActivity::class.java).apply {
                        putExtra("uploadId", upload.id)
                        putExtra("token", token)
                        putExtra("audioFilePath", "${requireActivity().externalCacheDir?.absolutePath}/audiorecordtest.mp3")
                    }
                    context.startActivity(intent)
                }
            }

            val hideFileButton = Button(context).apply {
                text = "Hide File"
                visibility = if (upload.hidden == false) View.VISIBLE else View.GONE
                setOnClickListener {
                    token?.let { it1 -> showHideConfirmationDialog(it1, upload.id) }
                }
            }

            val showFileButton = Button(context).apply {
                text = "Show File"
                visibility = if (upload.hidden == true) View.VISIBLE else View.GONE
                setOnClickListener {
                    token?.let { it1 -> showShowConfirmationDialog(it1, upload.id) }
                }
            }

            val deleteFileButton = Button(context).apply {
                text = "Delete File"
                setOnClickListener {
                    token?.let { it1 -> showDeleteConfirmationDialog(it1, upload.id, upload.latitude, upload.longitude) }
                }
            }

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(textView)
                addView(moreInfoButton)
                addView(hideFileButton)
                addView(showFileButton)
                addView(deleteFileButton)
            }
            uploadsContainer.addView(layout)
        }
    }

    private fun showDeleteConfirmationDialog(token: String, id: Int, latitude: Double, longitude: Double) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to delete this file?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteFile(token, id, latitude, longitude)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun showHideConfirmationDialog(token: String, id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to hide this file?")
            .setPositiveButton("Yes") { dialog, _ ->
                hideUpload(token, id)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun showShowConfirmationDialog(token: String, id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to show this file?")
            .setPositiveButton("Yes") { dialog, _ ->
                showUpload(token, id)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    @SuppressLint("SetTextI18n")
    private fun displayAllUploads(uploads: List<ResponseAllUploads>) {
        uploadsContainer2.removeAllViews()
        uploads.forEach { upload ->
            val textView = TextView(context).apply {
                text = """
                ID: ${upload.id}
                Longitude: ${upload.longitude}
                Latitude: ${upload.latitude}
                Possible Actions:
            """.trimIndent()
                setTextColor(resources.getColor(R.color.black, null))
                textSize = 16f
                setPadding(0, 16, 0, 16)
            }

            val moreInfoButton = Button(context).apply {
                text = "More Info"
                setOnClickListener {
                    val intent = Intent(context, MoreInfoActivity::class.java).apply {
                        putExtra("uploadId", upload.id)
                        putExtra("token", token)
                    }
                    context.startActivity(intent)
                }
            }

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(textView)
                addView(moreInfoButton)
            }

            uploadsContainer2.addView(layout)
        }
    }

    private fun deleteFromUploadData(username: String, latitude: Double, longitude: Double){
        uploadDataViewModel.delete(username, latitude, longitude)
    }

    private fun deleteFromInfoAudio(latitude: Double, longitude: Double){
        infoAudioViewModel.delete(latitude, longitude)
    }

    companion object {
        @JvmStatic
        fun newInstance(token: String, username: String) =
            MyUploadsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TOKEN, token)
                    putString(ARG_USERNAME, username)
                }
            }
    }
}
