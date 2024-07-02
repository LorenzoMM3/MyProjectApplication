package com.example.myprojectapplication

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LocalDbActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_db)
        val btnBack: Button = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}