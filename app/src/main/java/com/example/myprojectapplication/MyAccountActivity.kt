package com.example.myprojectapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.myprojectapplication.utility.ApiClient
import com.example.myprojectapplication.utility.ApiService
import com.example.myprojectapplication.utility.DeleteResponse
import com.example.myprojectapplication.utility.ResponseMyUploads
import com.example.myprojectapplication.utility.UtilNetwork
import com.example.myprojectapplication.utility.utilLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAccountActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiClient.instance.create(ApiService::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)
        UtilNetwork.checkConnection(this)

        val btnGoBack: Button = findViewById(R.id.btnBack1ma)
        val btnDeleteAccount: Button = findViewById(R.id.btnDeleteAccount)
        val textViewUser: TextView = findViewById(R.id.textView2ma)
        val textViewTotUploads: TextView = findViewById(R.id.textView5ma)
        val textViewClientId: TextView = findViewById(R.id.textView6ma)
        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")
        val clientId = intent.getIntExtra("clientId", -1)
        val clientToken = intent.getStringExtra("clientToken")

        textViewUser.text = "Username: $username"
        findTotalUploads(clientToken.toString(), object : UploadCountCallback {
            override fun onUploadCountReceived(count: Int) {
                textViewTotUploads.text = "Total Uploads: $count"
            }
        })
        textViewClientId.text = "Client ID: $clientId"

        btnGoBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("password", password)
            intent.putExtra("clientId", clientId)
            intent.putExtra("clientToken", clientToken)
            startActivity(intent)
        }

        btnDeleteAccount.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun findTotalUploads(clientToken: String, callback: UploadCountCallback) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.seeMyUploads("Bearer $clientToken")

        call.enqueue(object : Callback<List<ResponseMyUploads>> {
            override fun onResponse(call: Call<List<ResponseMyUploads>>, response: Response<List<ResponseMyUploads>>) {
                if (response.isSuccessful) {
                    val uploads = response.body()
                    val totalUploads = uploads?.size ?: 0
                    callback.onUploadCountReceived(totalUploads)
                } else {
                    val errorMessage: String = when (response.code()) {
                        401 -> {
                            utilLogin.forceLogin(this@MyAccountActivity)
                            "User is not authenticated"
                        }
                        else -> "Error: ${response.errorBody()?.string()}"
                    }
                    Toast.makeText(this@MyAccountActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<ResponseMyUploads>>, t: Throwable) {
                Toast.makeText(this@MyAccountActivity, "FetchMy Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface UploadCountCallback {
        fun onUploadCountReceived(count: Int)
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Yes") { dialog, id ->
                deleteUser()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun deleteUser() {
        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")
        val clientToken = intent.getStringExtra("clientToken")

        if (username.isNullOrEmpty() || password.isNullOrEmpty() || clientToken.isNullOrEmpty()) {
            Toast.makeText(this, "Username, password or token is null", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(this, LogActivity::class.java)
        val authHeader = "Bearer $clientToken"
        apiService.deleteUser(authHeader).enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("LogActivity", "Full response: ${response.toString()}")
                    Log.d("LogActivity", "Response Body: ${responseBody?.toString()}")

                    if (responseBody != null) {
                        Toast.makeText(this@MyAccountActivity, "Account Succesfully Deleted", Toast.LENGTH_LONG).show()
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this@MyAccountActivity, "Problem, Account Impossible to Delete", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                Toast.makeText(this@MyAccountActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                println("Error: ${t.message}")
            }
        })
    }

}