package com.example.myprojectapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UtilNetwork.checkConnection(this)

        val btnRecord: Button = findViewById(R.id.btnRecord)
        val btnMyUploads: Button = findViewById(R.id.btnMyUploads)
        val btnCityMap: Button = findViewById(R.id.btnCityMap)
        val welcomeMsg: TextView = findViewById(R.id.textViewWelcome)
        val btnHelp: Button = findViewById(R.id.btnHelp)
        val btnMyAccount: Button = findViewById(R.id.btnMyAccount)

        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")
        val clientId = intent.getIntExtra("clientId", -1)
        val clientToken = intent.getStringExtra("clientToken")

        val welcomeMsgString = welcomeMsg.text.toString()
        welcomeMsg.text = welcomeMsgString + username

        replaceFragment(RecordFragment.newInstance(clientToken ?: "", username ?: ""))

        btnRecord.setOnClickListener {
            replaceFragment(RecordFragment.newInstance(clientToken ?: "", username ?: ""))
        }

        btnMyUploads.setOnClickListener {
            replaceFragment(MyUploadsFragment.newInstance(clientToken ?: "", username ?: ""))
        }

        btnCityMap.setOnClickListener {
            replaceFragment(MapFragment.newInstance(clientToken ?: ""))
        }

        btnHelp.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("password", password)
            intent.putExtra("clientId", clientId)
            intent.putExtra("clientToken", clientToken)
            startActivity(intent)
        }

        btnMyAccount.setOnClickListener {
            val intent = Intent(this, MyAccountActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("password", password)
            intent.putExtra("clientId", clientId)
            intent.putExtra("clientToken", clientToken)
            startActivity(intent)
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView3, fragment)
            .commit()
    }

}