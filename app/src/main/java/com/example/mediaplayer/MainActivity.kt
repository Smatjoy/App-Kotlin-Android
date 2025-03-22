package com.example.mediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileFilter

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }

    private lateinit var rvSongs: RecyclerView
    private lateinit var tvNoPermission: TextView
    private lateinit var btnRequestPermission: Button
    private lateinit var mp3Adapter: Mp3Adapter
    private val mp3Files = mutableListOf<File>()

    // Aggiungi il MediaPlayerManager
    private lateinit var mediaPlayerManager: MediaPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inizializza il MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager(this)

        initViews()
        setupRecyclerView()
        checkPermission()
    }

    private fun initViews() {
        rvSongs = findViewById(R.id.rvSongs)
        tvNoPermission = findViewById(R.id.tvNoPermission)
        btnRequestPermission = findViewById(R.id.btnRequestPermission)

        btnRequestPermission.setOnClickListener {
            requestStoragePermission()
        }
    }

    private fun setupRecyclerView() {
        // Passa il MediaPlayerManager all'adapter
        mp3Adapter = Mp3Adapter(mp3Files, mediaPlayerManager)

        rvSongs.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mp3Adapter
        }
    }

    private fun checkPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
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

        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

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
                    this,
                    "Permesso necessario per accedere ai file audio",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadMp3Files() {
        // Ottieni tutti i file MP3 dalla memoria esterna
        val musicFiles = searchForMp3Files(Environment.getExternalStorageDirectory())

        mp3Files.clear()
        mp3Files.addAll(musicFiles)

        if (mp3Files.isEmpty()) {
            Toast.makeText(this, "Nessun file MP3 trovato", Toast.LENGTH_SHORT).show()
        }

        // Notifica l'adapter che i dati sono cambiati
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

    override fun onDestroy() {
        super.onDestroy()
        // Rilascia le risorse del MediaPlayer quando l'attività viene distrutta
        mediaPlayerManager.release()
    }
}