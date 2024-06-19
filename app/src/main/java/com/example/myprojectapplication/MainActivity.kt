package com.example.myprojectapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRecord: Button = findViewById(R.id.btnRecord)
        val btnMyUploads: Button = findViewById(R.id.btnMyUploads)
        val btnCityMap: Button = findViewById(R.id.btnCityMap)
        val welcomeMsg: TextView = findViewById(R.id.textViewWelcome)

        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")

        val welcomeMsgString = welcomeMsg.text.toString()
        welcomeMsg.text = welcomeMsgString + " " + username + " " + password

        btnRecord.setOnClickListener {
            replaceFragment(RecordFragment())
        }

        btnMyUploads.setOnClickListener {
            replaceFragment(MyUploadsFragment())
        }

        btnCityMap.setOnClickListener {
            replaceFragment(MapFragment())
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView3, fragment)
            .commit()
    }

}