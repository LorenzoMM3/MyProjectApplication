package com.example.myprojectapplication

import ApiService
import TokenRequest
import TokenResponse
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiClient.instance.create(ApiService::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnRegistration: Button = findViewById(R.id.btnRegister)
        val username: EditText = findViewById(R.id.edUsername2)
        val password: EditText = findViewById(R.id.edPassword2)

        btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val stringUser: String = username.text.toString().trim()
            val stringPsw: String = password.text.toString().trim()

            if(stringUser.isEmpty() || stringPsw.isEmpty()){
                Toast.makeText(this@LogActivity, "Username or Password null", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            intent.putExtra("username", stringUser)
            intent.putExtra("password", stringPsw)

            apiService.getToken(stringUser, stringPsw).enqueue(object : Callback<TokenResponse> {
                override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("LogActivity", "Full response: ${response.toString()}")
                        Log.d("LogActivity", "Response Body: ${responseBody?.toString()}")

                        val tokenResponse = response.body()
                        if (tokenResponse != null) {
                            val clientId = tokenResponse.client_id
                            val clientSecret = tokenResponse.client_secret
                            intent.putExtra("clientId", clientId)
                            intent.putExtra("clientToken", clientSecret)
                            Toast.makeText(this@LogActivity, "Successful Login", Toast.LENGTH_LONG).show()
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LogActivity, "Incorrect credentials", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(this@LogActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    println("Error: ${t.message}")
                }
            })
        }

        btnRegistration.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }

    }
}