package com.example.myprojectapplication.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.R
import com.example.myprojectapplication.database.UploadData

class UploadDataAdapter(private var dataList: List<UploadData>) : RecyclerView.Adapter<UploadDataAdapter.UserLocationViewHolder>() {

    class UserLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val latitude: TextView = itemView.findViewById(R.id.latitude)
        val longitude: TextView = itemView.findViewById(R.id.longitude)
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
    }

    override fun getItemCount() = dataList.size

    fun setData(newList: List<UploadData>) {
        dataList = newList
        notifyDataSetChanged()
    }
}
