package com.example.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.File

/**
 * Manages the playback of audio files using MediaPlayer.
 *
 * This class handles the initialization, playback, pausing, resuming, stopping, and releasing of
 * MediaPlayer resources. It supports playing audio from both File and Uri sources.
 *
 * @property context The application context.
 */
class MediaPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentFile: File? = null
    private var currentUri: Uri? = null
    private var isPaused = false




    /**
     * Plays the audio file provided.
     *
     * If a file is already playing, it will be stopped before playing the new file. If the same file
     * is already loaded and paused, it will resume playback. Otherwise, a new MediaPlayer is
     * initialized and prepared asynchronously.
     *
     * @param file The audio file to play.
     */
    fun play(file: File) {
        // If already playing, stop it.
        if (mediaPlayer?.isPlaying == true) {
            stop()
        }

        try {
            // If the same file and paused, resume.
            if (isPaused && file == currentFile) {
                mediaPlayer?.start()
                isPaused = false
                return
            }

            // Initialize a new MediaPlayer.
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

    /**
     * Plays audio from the provided Uri.
     *
     * Similar to [play], but uses a Uri as the source. Handles stopping current playback, resuming
     * if the same Uri is paused, and initializing MediaPlayer asynchronously.
     *
     * @param uri The Uri of the audio source to play.
     */
    fun playFromUri(uri: Uri) {
        // If already playing, stop it.
        if (mediaPlayer?.isPlaying == true) {
            stop()
        }

        try {
            // If the same URI and paused, resume.
            if (isPaused && uri == currentUri) {
                mediaPlayer?.start()
                isPaused = false
                return
            }

            // Initialize a new MediaPlayer.
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

    /**
     * Pauses the currently playing audio.
     *
     * If audio is playing, it will be paused and [isPaused] will be set to true.
     */
    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPaused = true
        }
    }

    /**
     * Resumes the currently paused audio.
     *
     * If audio is paused, it will resume playback and set [isPaused] to false.
     */
    fun resume() {
        if (isPaused) {
            mediaPlayer?.start()
            isPaused = false
        }
    }

    /**
     * Stops the currently playing audio.
     *
     * If audio is playing, it will be stopped and the MediaPlayer will be reset to its idle state.
     */
    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            reset()
        }
        isPaused = false
    }

    /**
     * Releases the MediaPlayer resources.
     *
     * This should be called when the MediaPlayer is no longer needed to free up resources. It stops
     * playback if necessary, releases the MediaPlayer, and clears related state variables.
     */
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

    /**
     * Checks if audio is currently playing.
     *
     * @return True if audio is playing, false otherwise.
     */
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
    class PlayerFragment : Fragment() {

        private lateinit var mediaPlayerManager: MediaPlayerManager

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_player, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            mediaPlayerManager = MediaPlayerManager(requireContext())

            val btnPlay = view.findViewById<Button>(R.id.playButton)
            val btnPause = view.findViewById<Button>(R.id.pauseButton)
            val btnStop = view.findViewById<Button>(R.id.stopButton)

            val audioFile = File(requireContext().filesDir, "sample_audio.mp3")

            btnPlay.setOnClickListener { mediaPlayerManager.play(audioFile) }
            btnPause.setOnClickListener {
                mediaPlayerManager.pause()

            }
            btnStop.setOnClickListener { mediaPlayerManager.stop() }
        }
    }
}