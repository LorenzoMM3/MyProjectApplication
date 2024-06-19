package com.example.myprojectapplication

import ApiService
import DeleteRequest
import DeleteResponse
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAccountActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiClient.instance.create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        val btnGoBack: Button = findViewById(R.id.btnBack1ma)
        val btnDeleteAccount: Button = findViewById(R.id.btnDeleteAccount)
        val textViewUser: TextView = findViewById(R.id.textView2ma)
        val textViewPsw: TextView = findViewById(R.id.textView3ma)
        val textViewTotUploads: TextView = findViewById(R.id.textView5ma)
        val textViewClientId: TextView = findViewById(R.id.textView6ma)
        val textViewToken: TextView = findViewById(R.id.textView6ma2)
        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")
        val clientId = intent.getIntExtra("clientId", -1)
        val clientToken = intent.getStringExtra("clientToken")

        textViewUser.text = "Username: $username"
        textViewPsw.text = "Password: $password"
        textViewTotUploads.text = "Total Uploads: 0"
        textViewClientId.text = "Client ID: $clientId"
        textViewToken.text = "Client Token: $clientToken"

        btnGoBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("password", password)
            intent.putExtra("clientId", clientId)
            intent.putExtra("clientToken", clientToken)
            startActivity(intent)
        }

        btnDeleteAccount.setOnClickListener {

            if (username.isNullOrEmpty() || password.isNullOrEmpty() || clientToken.isNullOrEmpty()) {
                Toast.makeText(this, "Username, password e token non possono essere vuoti", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val intent = Intent(this, LogActivity::class.java)

            val authHeader = "Bearer $clientToken"
            val deleteRequest = DeleteRequest(username, password)
            apiService.deleteUser(authHeader, deleteRequest).enqueue(object : Callback<DeleteResponse> {
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
}