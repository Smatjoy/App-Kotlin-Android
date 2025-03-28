package com.example.mediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileFilter

class HomeFragment : Fragment() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }

    private lateinit var rvSongs: RecyclerView
    private lateinit var tvNoPermission: TextView
    private lateinit var btnRequestPermission: Button
    private lateinit var mp3Adapter: Mp3Adapter
    private val mp3Files = mutableListOf<Song>()
    private lateinit var mediaPlayerManager: MediaPlayerManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ottieni il MediaPlayerManager dall'Activity
        mediaPlayerManager = (requireActivity() as MainActivity).mediaPlayerManager

        initViews(view)
        setupRecyclerView()
        checkPermission()
    }

    private fun initViews(view: View) {
        rvSongs = view.findViewById(R.id.rvSongs)
        tvNoPermission = view.findViewById(R.id.tvNoPermission)
        btnRequestPermission = view.findViewById(R.id.btnRequestPermission)

        btnRequestPermission.setOnClickListener {
            requestStoragePermission()
        }
    }

    private fun setupRecyclerView() {
        // The adapter receives a list of Song objects
        mp3Adapter = Mp3Adapter(mp3Files, mediaPlayerManager)

        rvSongs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mp3Adapter
        }
    }

    private fun checkPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            // Permesso già concesso
            loadMp3Files()
        } else {
            // Mostra UI per richiedere il permesso
            showPermissionUI()
        }
    }

    private fun showPermissionUI() {
        rvSongs.visibility = View.GONE
        tvNoPermission.visibility = View.VISIBLE
        btnRequestPermission.visibility = View.VISIBLE
    }

    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        requestPermissions(
            arrayOf(permission),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permesso concesso
                rvSongs.visibility = View.VISIBLE
                tvNoPermission.visibility = View.GONE
                btnRequestPermission.visibility = View.GONE
                loadMp3Files()
            } else {
                // Permesso negato
                Toast.makeText(
                    requireContext(),
                    "Permesso necessario per accedere ai file audio",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun loadMp3Files() {
        // Get all MP3 files from external storage
        val musicFiles = searchForMp3Files(Environment.getExternalStorageDirectory())

        // Convert File objects to Song objects
        val songList = musicFiles.map { file ->
            Mp3MetadataExtractor.extractMetadata(requireContext(), file)
        }

        mp3Files.clear()
        mp3Files.addAll(songList)

        if (mp3Files.isEmpty()) {
            Toast.makeText(requireContext(), "Nessun file MP3 trovato", Toast.LENGTH_SHORT).show()
        }

        // Notify the adapter that data has changed
        mp3Adapter.notifyDataSetChanged()
    }

    private fun searchForMp3Files(directory: File): List<File> {
        val mp3FileList = mutableListOf<File>()

        // Filtro per i file MP3
        val mp3Filter = FileFilter { file ->
            file.isDirectory || file.name.lowercase().endsWith(".mp3")
        }

        val files = directory.listFiles(mp3Filter)

        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    // Ricerca ricorsiva nelle sottodirectory
                    mp3FileList.addAll(searchForMp3Files(file))
                } else {
                    // Aggiungi il file MP3 alla lista
                    mp3FileList.add(file)
                }
            }
        }

        return mp3FileList
    }
}