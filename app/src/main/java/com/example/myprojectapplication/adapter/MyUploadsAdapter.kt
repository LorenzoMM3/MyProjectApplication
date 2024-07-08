package com.example.myprojectapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.myprojectapplication.MoreInfoActivity
import com.example.myprojectapplication.R
import com.example.myprojectapplication.utility.ResponseMyUploads

class MyUploadsAdapter(
    private val context: Context,
    private val token: String,
    private val uploads: List<ResponseMyUploads>,
    private val deleteFileCallback: (String, Int, Double, Double) -> Unit,
    private val showUploadCallback: (String, Int) -> Unit,
    private val hideUploadCallback: (String, Int) -> Unit
) : RecyclerView.Adapter<MyUploadsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val uploadTextView: TextView = view.findViewById(R.id.uploadTextView)
        val moreInfoButton: Button = view.findViewById(R.id.moreInfoButton)
        val hideFileButton: Button = view.findViewById(R.id.hideFileButton)
        val showFileButton: Button = view.findViewById(R.id.showFileButton)
        val deleteFileButton: Button = view.findViewById(R.id.deleteFileButton)
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
            Hidden: ${upload.hidden}
            Possible Actions: 
        """.trimIndent()

        holder.moreInfoButton.setOnClickListener {
            val intent = Intent(context, MoreInfoActivity::class.java).apply {
                putExtra("uploadId", upload.id)
                putExtra("token", token)
            }
            context.startActivity(intent)
        }

        holder.hideFileButton.visibility = if (upload.hidden == false) View.VISIBLE else View.GONE
        holder.showFileButton.visibility = if (upload.hidden == true) View.VISIBLE else View.GONE
        holder.deleteFileButton.visibility = View.VISIBLE

        holder.hideFileButton.setOnClickListener {
            showHideConfirmationDialog(upload.id)
        }

        holder.showFileButton.setOnClickListener {
            showShowConfirmationDialog(upload.id)
        }

        holder.deleteFileButton.setOnClickListener {
            showDeleteConfirmationDialog(upload.id, upload.latitude, upload.longitude)
        }
    }

    override fun getItemCount(): Int = uploads.size

    private fun showDeleteConfirmationDialog(id: Int, latitude: Double, longitude: Double) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to delete this file?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteFileCallback(token, id, latitude, longitude)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun showHideConfirmationDialog(id: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to hide this file?")
            .setPositiveButton("Yes") { dialog, _ ->
                hideUploadCallback(token, id)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun showShowConfirmationDialog(id: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to show this file?")
            .setPositiveButton("Yes") { dialog, _ ->
                showUploadCallback(token, id)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}
