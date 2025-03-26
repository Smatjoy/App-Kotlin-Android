package com.example.mediaplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class Mp3Adapter(
    private val songs: List<Song>,
    private val mediaPlayerManager: MediaPlayerManager
) : RecyclerView.Adapter<Mp3Adapter.Mp3ViewHolder>() {

    // Tieni traccia della posizione dell'elemento attualmente in riproduzione
    private var currentPlayingPosition = -1

    inner class Mp3ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtFileName: TextView = itemView.findViewById(R.id.txtFileName)
        val txtArtist: TextView = itemView.findViewById(R.id.txtArtist)
        val txtAlbum: TextView = itemView.findViewById(R.id.txtAlbum)
        val imgAlbumArt: ImageView = itemView.findViewById(R.id.imgAlbumArt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Mp3ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mp3, parent, false)
        return Mp3ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Mp3ViewHolder, position: Int) {
        val song = songs[position]
        holder.txtFileName.text = song.title
        holder.txtArtist.text = song.artist
        holder.txtAlbum.text = song.album

        // Imposta la cover dell'album se disponibile, altrimenti usa l'icona predefinita
        if (song.albumArt != null) {
            holder.imgAlbumArt.setImageBitmap(song.albumArt)
        } else {
            holder.imgAlbumArt.setImageResource(R.drawable.default_album_art)
        }

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
                mediaPlayerManager.playFromUri(song.uri)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = songs.size
}