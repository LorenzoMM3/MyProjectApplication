package com.example.myprojectapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnRegistration: Button = findViewById(R.id.btnRegister)
        val username: EditText = findViewById(R.id.edUsername2)
        val password: EditText = findViewById(R.id.edPassword2)

        btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("username", username.text.toString())
            intent.putExtra("password", password.text.toString())
            Toast.makeText(this, username.text.toString(), Toast.LENGTH_SHORT).show()
            Toast.makeText(this, password.text.toString(), Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        btnRegistration.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }

    }
}