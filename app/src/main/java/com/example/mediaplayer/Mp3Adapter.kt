package com.example.mediaplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class Mp3Adapter(
    private val mp3Files: List<File>,
    private val onItemClick: (File) -> Unit
) : RecyclerView.Adapter<Mp3Adapter.Mp3ViewHolder>() {

    inner class Mp3ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Usiamo l'ID corretto definito nel layout
        val txtFileName: TextView = itemView.findViewById(R.id.txtFileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Mp3ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mp3, parent, false)
        return Mp3ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Mp3ViewHolder, position: Int) {
        val file = mp3Files[position]
        holder.txtFileName.text = file.name
        holder.itemView.setOnClickListener { onItemClick(file) }
    }

    override fun getItemCount(): Int = mp3Files.size
}