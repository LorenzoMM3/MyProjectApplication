package com.example.myprojectapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.adapter.AllUploadsAdapter
import com.example.myprojectapplication.adapter.MyUploadsAdapter
import com.example.myprojectapplication.database.AppDatabase
import com.example.myprojectapplication.repository.InfoAudioRepository
import com.example.myprojectapplication.utility.utilLogin.forceLogin
import com.example.myprojectapplication.utility.ApiClient
import com.example.myprojectapplication.utility.ApiService
import com.example.myprojectapplication.utility.ResponseAllUploads
import com.example.myprojectapplication.utility.ResponseDeleteFile
import com.example.myprojectapplication.utility.ResponseHideFile
import com.example.myprojectapplication.utility.ResponseMyUploads
import com.example.myprojectapplication.utility.ResponseShowFile
import com.example.myprojectapplication.utility.UtilNetwork
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
    private lateinit var recyclerViewMyUploads: RecyclerView
    private lateinit var recyclerViewAllUploads: RecyclerView
    private lateinit var buttonLocalDb: Button
    private lateinit var btnInfoAudio: Button

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
        recyclerViewMyUploads = view.findViewById(R.id.recyclerViewMyUploads)
        recyclerViewAllUploads = view.findViewById(R.id.recyclerViewAllUploads)
        buttonLocalDb = view.findViewById(R.id.buttonLocalDb)
        btnInfoAudio = view.findViewById(R.id.buttonLocalDb2)

        recyclerViewMyUploads.layoutManager = LinearLayoutManager(context)
        recyclerViewAllUploads.layoutManager = LinearLayoutManager(context)

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
                        else -> "Error: ${response.errorBody()?.string()}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<ResponseAllUploads>>, t: Throwable) {
                Toast.makeText(context, "FetchAll Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayMyUploads(uploads: List<ResponseMyUploads>) {
        val adapter = MyUploadsAdapter(requireContext(), token!!, uploads,
            { token, id, latitude, longitude -> deleteFile(token, id, latitude, longitude) },
            { token, id -> showUpload(token, id) },
            { token, id -> hideUpload(token, id) }
        )
        recyclerViewMyUploads.adapter = adapter
    }

    private fun displayAllUploads(uploads: List<ResponseAllUploads>) {
        val adapter = AllUploadsAdapter(requireContext(), token!!, uploads)
        recyclerViewAllUploads.adapter = adapter
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
                        else -> "Error: ${response.errorBody()?.string()}"
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
                        else -> "Error: ${response.errorBody()?.string()}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseHideFile>, t: Throwable) {
                Toast.makeText(context, "Hide Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
