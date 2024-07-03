package com.example.myprojectapplication

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.database.InfoAudio

class InfoAudioAdapter : ListAdapter<InfoAudio, InfoAudioAdapter.InfoAudioViewHolder>(InfoAudioComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoAudioViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_info_audio, parent, false)
        return InfoAudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoAudioViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class InfoAudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val infoTextView: TextView = itemView.findViewById(R.id.infoTextView)

        @SuppressLint("SetTextI18n")
        fun bind(infoAudio: InfoAudio) {
            infoTextView.text = "Longitude: ${infoAudio.longitude}, Latitude: ${infoAudio.latitude}, BPM: ${infoAudio.bpm}, " +
                    "Danceability: ${infoAudio.danceability}, Loudness: ${infoAudio.loudness}, Mood: ${infoAudio.mood}, " +
                    "Genre: ${infoAudio.genre}, Instrument: ${infoAudio.instrument}, Audio File Path: ${infoAudio.audioFilePath}"
        }
    }

    class InfoAudioComparator : DiffUtil.ItemCallback<InfoAudio>() {
        override fun areItemsTheSame(oldItem: InfoAudio, newItem: InfoAudio): Boolean {
            return oldItem.longitude == newItem.longitude && oldItem.latitude == newItem.latitude
        }

        override fun areContentsTheSame(oldItem: InfoAudio, newItem: InfoAudio): Boolean {
            return oldItem == newItem
        }
    }
}
