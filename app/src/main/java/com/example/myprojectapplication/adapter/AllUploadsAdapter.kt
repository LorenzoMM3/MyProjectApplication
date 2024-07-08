package com.example.myprojectapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.MoreInfoActivity
import com.example.myprojectapplication.R
import com.example.myprojectapplication.utility.ResponseAllUploads

class AllUploadsAdapter(
    private val context: Context,
    private val token: String,
    private val uploads: List<ResponseAllUploads>
) : RecyclerView.Adapter<AllUploadsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val uploadTextView: TextView = view.findViewById(R.id.uploadTextView)
        val moreInfoButton: Button = view.findViewById(R.id.moreInfoButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_upload, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val upload = uploads[position]
        holder.uploadTextView.text = """
            ID: ${upload.id}
            Longitude: ${upload.longitude}
            Latitude: ${upload.latitude}
            Possible Actions:
        """.trimIndent()

        holder.moreInfoButton.setOnClickListener {
            val intent = Intent(context, MoreInfoActivity::class.java).apply {
                putExtra("uploadId", upload.id)
                putExtra("token", token)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = uploads.size
}
