package com.example.myprojectapplication

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.database.UploadDataAdapter
import com.example.myprojectapplication.database.UploadDataApp

class LocalDbActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UploadDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_db)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UploadDataAdapter(emptyList())

        recyclerView.adapter = adapter

        val database = UploadDataApp.getDatabase(this)
        val uploadDataDao = database.uploadDataDao()

        uploadDataDao.getAllUploadData().observe(this, Observer { dataList ->
            adapter.setData(dataList)
        })

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed()
        }

    }
}
