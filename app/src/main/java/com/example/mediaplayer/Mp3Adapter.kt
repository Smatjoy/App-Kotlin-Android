package com.example.mediaplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class Mp3Adapter(
    private val mp3Files: List<File>,
    private val mediaPlayerManager: MediaPlayerManager
) : RecyclerView.Adapter<Mp3Adapter.Mp3ViewHolder>() {

    // Tieni traccia della posizione dell'elemento attualmente in riproduzione
    private var currentPlayingPosition = -1

    inner class Mp3ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtFileName: TextView = itemView.findViewById(R.id.txtFileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Mp3ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mp3, parent, false)
        return Mp3ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Mp3ViewHolder, position: Int) {
        val file = mp3Files[position]
        holder.txtFileName.text = file.name

        // Evidenzia l'elemento attualmente in riproduzione
        if (position == currentPlayingPosition && mediaPlayerManager.isPlaying()) {
            holder.itemView.setBackgroundResource(R.color.selected_item_background)
        } else {
            holder.itemView.setBackgroundResource(android.R.color.transparent)
        }

        holder.itemView.setOnClickListener {
            // Se clicchiamo sullo stesso elemento già in riproduzione, mettiamo in pausa
            if (position == currentPlayingPosition && mediaPlayerManager.isPlaying()) {
                mediaPlayerManager.pause()
                notifyItemChanged(position)
            } else {
                // Altrimenti riproduciamo il nuovo file
                val previousPosition = currentPlayingPosition
                currentPlayingPosition = position

                // Aggiorna l'elemento precedentemente in riproduzione
                if (previousPosition != -1) {
                    notifyItemChanged(previousPosition)
                }

                // Riproduci il file e aggiorna l'elemento corrente
                mediaPlayerManager.play(file)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = mp3Files.size
}