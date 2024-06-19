package com.example.myprojectapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

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
            val stringUser: String = username.text.toString()
            val stringPsw: String = password.text.toString()
            // salvare i dati

            startActivity(intent)
        }

        btnLogin2.setOnClickListener {
            val intent = Intent(this, LogActivity::class.java)
            startActivity(intent)
        }

    }
}