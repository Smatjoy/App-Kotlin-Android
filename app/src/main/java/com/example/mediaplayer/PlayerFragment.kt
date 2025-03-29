/*Fraagments Are

 */

package com.example.mediaplayer

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import java.io.File


/*
Inflates fragment_player.xml to use it inside this Fragment.

Finds the buttons (Play, Pause, Stop).

Creates a MediaPlayerManager instance to control audio playback.

Handles button clicks:
 */
class PlayerFragment : Fragment() {

    private lateinit var mediaPlayerManager: MediaPlayerManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Link this fragment to fragment_player.xml
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager(requireContext())

        // Find buttons by their IDs
        val btnPlay = view.findViewById<Button>(R.id.playButton)
        val btnPause = view.findViewById<Button>(R.id.pauseButton)
        val btnStop = view.findViewById<Button>(R.id.stopButton)

        // Sample audio file (replace with actual file path)
        val audioFile = File(requireContext().filesDir, "sample_audio.mp3")

        // Set click listeners
        btnPlay.setOnClickListener {
            mediaPlayerManager.play(audioFile)
        }

        btnPause.setOnClickListener {
            mediaPlayerManager.pause()
        }

        btnStop.setOnClickListener {
            mediaPlayerManager.stop()
        }
    }
}
