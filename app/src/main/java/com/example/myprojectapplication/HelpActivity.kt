package com.example.myprojectapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myprojectapplication.utility.UtilNetwork

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        UtilNetwork.checkConnection(this)

        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")
        val clientId = intent.getIntExtra("clientId", -1)
        val clientToken = intent.getStringExtra("clientToken")
        val btnGoBack1h: Button = findViewById(R.id.btnBack1h)

        btnGoBack1h.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("password", password)
            intent.putExtra("clientId", clientId)
            intent.putExtra("clientToken", clientToken)
            startActivity(intent)
        }

    }
}