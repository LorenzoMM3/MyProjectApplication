package com.example.myprojectapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MoreInfoAdapter(private val infoList: List<String>) : RecyclerView.Adapter<MoreInfoAdapter.MoreInfoViewHolder>() {

    class MoreInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val infoTextView: TextView = itemView.findViewById(R.id.infoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_more_info, parent, false)
        return MoreInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoreInfoViewHolder, position: Int) {
        holder.infoTextView.text = infoList[position]
    }

    override fun getItemCount(): Int {
        return infoList.size
    }
}
