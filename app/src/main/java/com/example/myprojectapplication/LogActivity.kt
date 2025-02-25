package com.example.myprojectapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.Manifest
import com.example.myprojectapplication.notification.NetworkChangeReceiver
import com.example.myprojectapplication.utility.ApiClient
import com.example.myprojectapplication.utility.ApiService
import com.example.myprojectapplication.utility.TokenResponse
import com.example.myprojectapplication.utility.UtilNetwork

class LogActivity : AppCompatActivity() {

    private val REQUEST_CODE_POST_NOTIFICATIONS = 1
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var clientToken:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        UtilNetwork.checkConnection(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnRegistration: Button = findViewById(R.id.btnRegister)
        val username: EditText = findViewById(R.id.edUsername2)
        val password: EditText = findViewById(R.id.edPassword2)

        btnLogin.setOnClickListener {
            UtilNetwork.checkConnection(this)
            val intent = Intent(this, MainActivity::class.java)
            val stringUser: String = username.text.toString().trim()
            val stringPsw: String = password.text.toString().trim()

            if(stringUser.isEmpty() || stringPsw.isEmpty()){
                Toast.makeText(this@LogActivity, "Username or Password null", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            intent.putExtra("username", stringUser)
            intent.putExtra("password", stringPsw)

            val apiService = ApiClient.instance.create(ApiService::class.java)
            apiService.getToken(stringUser, stringPsw).enqueue(object : Callback<TokenResponse> {
                override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.isSuccessful) {
                        val tokenResponse = response.body()
                        if (tokenResponse != null) {
                            val clientId = tokenResponse.client_id
                            clientToken = tokenResponse.client_secret
                            intent.putExtra("clientId", clientId)
                            intent.putExtra("clientToken", clientToken)
                            Toast.makeText(this@LogActivity, "Successful Login", Toast.LENGTH_LONG).show()
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val errorMessage: String = when (response.code()) {
                            400 -> "Incorrect username or password"
                            else -> "Error: ${response.errorBody()?.string()}"
                        }
                        Toast.makeText(this@LogActivity, errorMessage, Toast.LENGTH_SHORT).show()
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