package com.example.myprojectapplication

import ApiService
import ResponseAllUploads
import ResponseMyUploads
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_TOKEN = "token"

class MyUploadsFragment : Fragment() {
    private var token: String? = null
    private lateinit var uploadsContainer: LinearLayout
    private lateinit var uploadsContainer2: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(ARG_TOKEN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_uploads, container, false)
        uploadsContainer = view.findViewById(R.id.uploadsContainer)
        uploadsContainer2 = view.findViewById(R.id.uploadsContainer2)

        token?.let { fetchMyUploads(it) }
        token?.let { fetchAllUploads(it) }

        return view
    }

    private fun fetchMyUploads(token: String) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.SeeMyUploads("Bearer $token")

        call.enqueue(object : Callback<List<ResponseMyUploads>> {
            override fun onResponse(call: Call<List<ResponseMyUploads>>, response: Response<List<ResponseMyUploads>>) {
                if (response.isSuccessful) {
                    val uploads = response.body()
                    uploads?.let {
                        displayUploads(it)
                    }
                } else {
                    Toast.makeText(context, "Errore: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ResponseMyUploads>>, t: Throwable) {
                Toast.makeText(context, "Errore: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAllUploads(token: String) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.SeeAllUploads("Bearer $token")

        call.enqueue(object : Callback<List<ResponseAllUploads>> {
            override fun onResponse(call: Call<List<ResponseAllUploads>>, response: Response<List<ResponseAllUploads>>) {
                if (response.isSuccessful) {
                    val uploads = response.body()
                    uploads?.let {
                        displayAllUploads(it)
                    }
                } else {
                    Toast.makeText(context, "Errore: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ResponseAllUploads>>, t: Throwable) {
                Toast.makeText(context, "Errore: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun displayUploads(uploads: List<ResponseMyUploads>) {
        uploadsContainer.removeAllViews()
        uploads.forEach { upload ->
            val textView = TextView(context).apply {
                text = """
                    ID: ${upload.id}
                    Longitude: ${upload.longitude}
                    Latitude: ${upload.latitude}
                    Hidden: ${upload.hidden}
                    Uploaded: ${upload.uploaded}
                """.trimIndent()
                setTextColor(resources.getColor(R.color.black, null))
                textSize = 16f
                setPadding(0, 16, 0, 16)
            }
            uploadsContainer.addView(textView)
        }
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
                """.trimIndent()
                setTextColor(resources.getColor(R.color.black, null))
                textSize = 16f
                setPadding(0, 16, 0, 16)
            }
            uploadsContainer2.addView(textView)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(token: String) =
            MyUploadsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TOKEN, token)
                }
            }
    }
}