package com.example.myprojectapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)


        val btnRegister2: Button = findViewById(R.id.btnRegister2)
        val btnLogin2: Button = findViewById(R.id.btnLogin2)
        val username: EditText = findViewById(R.id.edUsername2)
        val password: EditText = findViewById(R.id.edPassword2)

        btnRegister2.setOnClickListener {
            val intent = Intent(this, LogActivity::class.java)
            val stringUser: String = username.text.toString().trim()
            val stringPsw: String = password.text.toString().trim()

            if(stringUser.isEmpty() || stringPsw.isEmpty()){
                Toast.makeText(this@RegActivity, "Username or Password null", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val apiService = ApiClient.instance.create(ApiService::class.java)
            val request = SignUpRequest(username = stringUser, password = stringPsw)
            apiService.signUp(request).enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.isSuccessful) {
                        val signUpResponse = response.body()
                        Toast.makeText(this@RegActivity, "User signed up: ${signUpResponse?.username}", Toast.LENGTH_LONG).show()
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@RegActivity, "Sign up failed", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    Toast.makeText(this@RegActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    println("Error: ${t.message}")
                }
            })
        }

        btnLogin2.setOnClickListener {
            val intent = Intent(this, LogActivity::class.java)
            startActivity(intent)
        }

    }
}