package com.example.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import java.io.File

class MediaPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentFile: File? = null
    private var currentUri: Uri? = null
    private var isPaused = false

    fun play(file: File) {
        // Se è già in riproduzione, ferma
        if (mediaPlayer?.isPlaying == true) {
            stop()
        }

        try {
            // Se è lo stesso file e in pausa, riprendi
            if (isPaused && file == currentFile) {
                mediaPlayer?.start()
                isPaused = false
                return
            }

            // Inizializza un nuovo MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.fromFile(file))
                setOnPreparedListener { mp ->
                    mp.start()
                    isPaused = false
                }
                setOnErrorListener { _, what, extra ->
                    Toast.makeText(
                        context,
                        "Errore di riproduzione: $what, $extra",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                prepareAsync()
            }
            currentFile = file
            currentUri = Uri.fromFile(file)

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Errore: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    fun playFromUri(uri: Uri) {
        // Se è già in riproduzione, ferma
        if (mediaPlayer?.isPlaying == true) {
            stop()
        }

        try {
            // Se è lo stesso URI e in pausa, riprendi
            if (isPaused && uri == currentUri) {
                mediaPlayer?.start()
                isPaused = false
                return
            }

            // Inizializza un nuovo MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                setOnPreparedListener { mp ->
                    mp.start()
                    isPaused = false
                }
                setOnErrorListener { _, what, extra ->
                    Toast.makeText(
                        context,
                        "Errore di riproduzione: $what, $extra",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                prepareAsync()
            }
            currentUri = uri
            currentFile = null  // Reset the file reference since we're using URI directly

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Errore: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPaused = true
        }
    }

    fun resume() {
        if (isPaused) {
            mediaPlayer?.start()
            isPaused = false
        }
    }

    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            reset()
        }
        isPaused = false
    }

    fun release() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        currentFile = null
        currentUri = null
        isPaused = false
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}