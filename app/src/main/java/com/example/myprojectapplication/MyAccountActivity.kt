package com.example.myprojectapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MyAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        val btnGoBack: Button = findViewById(R.id.btnBack1ma)
        val btnDeleteAccount: Button = findViewById(R.id.btnDeleteAccount)
        val textViewUser: TextView = findViewById(R.id.textView2ma)
        val textViewUserString: String = textViewUser.text.toString()
        val textViewPsw: TextView = findViewById(R.id.textView3ma)
        val textViewPswString: String = textViewPsw.text.toString()
        val textViewTotUploads: TextView = findViewById(R.id.textView5ma)
        val textViewTotUploadsString: String = textViewTotUploads.text.toString()
        val textViewAltro: TextView = findViewById(R.id.textView6ma)
        val textViewAltroString: String = textViewAltro.text.toString()
        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")

        textViewUser.text = textViewUserString + username
        textViewPsw.text = textViewPswString + password
        textViewTotUploads.text = textViewTotUploadsString + "0"
        textViewAltro.text = textViewAltroString + "Altro"

        btnGoBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("password", password)
            startActivity(intent)
        }

        btnDeleteAccount.setOnClickListener {
            val intent = Intent(this, LogActivity::class.java)
            // eliminazione della'account

            startActivity(intent)
        }

    }
}