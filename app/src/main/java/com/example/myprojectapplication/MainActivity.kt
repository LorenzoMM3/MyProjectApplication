package com.example.myprojectapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRecord: Button = findViewById(R.id.btnRecord)
        val btnMyUploads: Button = findViewById(R.id.btnMyUploads)
        val btnCityMap: Button = findViewById(R.id.btnCityMap)

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