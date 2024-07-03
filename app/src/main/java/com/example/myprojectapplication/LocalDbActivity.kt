package com.example.myprojectapplication

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.database.UploadDataAdapter
import com.example.myprojectapplication.database.UploadDataApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocalDbActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnDeleteAll: Button
    private lateinit var adapter: UploadDataAdapter
    private lateinit var database: UploadDataApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_db)

        database = UploadDataApp.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerView)
        btnDeleteAll = findViewById(R.id.btnDeleteAll)
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

        btnDeleteAll.setOnClickListener {
            deleteAllFromDb()
        }

    }

    private fun deleteAllFromDb() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete all files from this Db?")
            .setPositiveButton("Yes") { dialog, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.uploadDataDao().deleteAll()
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LocalDbActivity, "All elements deleted from DB", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

}
