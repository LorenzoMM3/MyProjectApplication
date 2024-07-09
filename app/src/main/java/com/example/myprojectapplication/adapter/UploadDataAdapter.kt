package com.example.myprojectapplication.adapter

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.R
import com.example.myprojectapplication.database.UploadData
import java.io.File

class UploadDataAdapter(
    private var dataList: List<UploadData>,
    private val onPlayClick: (UploadData) -> Unit,
    private val onPauseClick: (UploadData) -> Unit,
    private val onUploadClick: (UploadData) -> Unit,
    private val onDeleteClick: (UploadData) -> Unit
) : RecyclerView.Adapter<UploadDataAdapter.UserLocationViewHolder>() {

    class UserLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val latitude: TextView = itemView.findViewById(R.id.latitude)
        val longitude: TextView = itemView.findViewById(R.id.longitude)
        val btnPlayAudio: Button = itemView.findViewById(R.id.btnPlayAudio)
        val btnPauseAudio: Button = itemView.findViewById(R.id.btnPauseAudio)
        val btnUploadAudio: Button = itemView.findViewById(R.id.btnUploadAudio)
        val btnDeleteAudio: Button = itemView.findViewById(R.id.btnDeleteAudio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserLocationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_upload_data, parent, false)
        return UserLocationViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserLocationViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.username.text = "Username: ${currentItem.username}"
        holder.latitude.text = "Latitude: ${currentItem.latitude}"
        holder.longitude.text = "Longitude: ${currentItem.longitude}"

        holder.btnPlayAudio.setOnClickListener {
            onPlayClick(currentItem)
        }

        holder.btnPauseAudio.setOnClickListener {
            onPauseClick(currentItem)
        }

        holder.btnUploadAudio.setOnClickListener {
            onUploadClick(currentItem)
        }

        holder.btnDeleteAudio.setOnClickListener {
            onDeleteClick(currentItem)
        }
    }

    override fun getItemCount() = dataList.size

    fun setData(newList: List<UploadData>) {
        dataList = newList
        notifyDataSetChanged()
    }
}
