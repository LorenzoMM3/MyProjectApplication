package com.example.myprojectapplication

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.database.InfoAudioAdapter
import com.example.myprojectapplication.viewmodel.InfoAudioViewModel
import com.example.myprojectapplication.viewmodel.InfoAudioViewModelFactory
import com.example.myprojectapplication.repository.InfoAudioRepository
import com.example.myprojectapplication.database.InfoAudioApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoAudioActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InfoAudioAdapter
    private val infoAudioViewModel: InfoAudioViewModel by viewModels {
        val dao = InfoAudioApp.getDatabase(application).infoAudioDao()
        val repository = InfoAudioRepository(dao)
        InfoAudioViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_audio)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = InfoAudioAdapter()
        recyclerView.adapter = adapter

        infoAudioViewModel.allInfoAudio.observe(this, Observer { infoAudios ->
            infoAudios?.let { adapter.submitList(it) }
        })

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            if(!UtilNetwork.isNetworkAvailable(this)){
                val builder = AlertDialog.Builder(this)
                builder.setMessage("You still have no internet connection. Please restore the connection and press 'Go Back' to use the app.")
                    .setNegativeButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            } else {
                onBackPressed()
            }
        }

        val btnDeleteAll: Button = findViewById(R.id.btnDeleteAll)
        btnDeleteAll.setOnClickListener {
            deleteAllFromDb()
        }

        if (!UtilNetwork.isNetworkAvailable(this)) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("No internet connection. Here you can see your uploads and play the audios. Please restore the connection and press 'Go Back' to use the app.")
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun deleteAllFromDb() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete all files from this Db? You won't be able to bring them back.")
            .setPositiveButton("Yes") { dialog, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val db = InfoAudioApp.getDatabase(this@InfoAudioActivity)
                        db.infoAudioDao().deleteAll()
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@InfoAudioActivity, "All elements deleted from DB", Toast.LENGTH_SHORT).show()
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
