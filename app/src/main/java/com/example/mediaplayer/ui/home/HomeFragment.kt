package com.example.mediaplayer.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.MediaPlayerManager
import com.example.mediaplayer.Mp3Adapter
import com.example.mediaplayer.databinding.FragmentHomeBinding
import java.io.File
import java.io.FileFilter

class HomeFragment : Fragment() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mp3Adapter: Mp3Adapter
    private val mp3Files = ArrayList<File>()
    private lateinit var mediaPlayerManager: MediaPlayerManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inizializza MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager(requireContext())

        // Configura il TextView
        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.textHome.text = it
        }

        // Configura la RecyclerView
        setupRecyclerView()

        // Verifica i permessi
        checkPermission()

        return root
    }

    private fun checkPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED) {
            // Permesso già concesso
            loadMp3Files()
        } else {
            // Richiedi il permesso
            requestPermissions(arrayOf(permission), PERMISSION_REQUEST_CODE)
        }
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
                loadMp3Files()
            } else {
                // Permesso negato
                Toast.makeText(
                    requireContext(),
                    "Permesso necessario per accedere ai file audio",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupRecyclerView() {
        // Crea l'adapter con MediaPlayerManager
        mp3Adapter = Mp3Adapter(mp3Files, mediaPlayerManager)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mp3Adapter
        }
    }

    private fun loadMp3Files() {
        // Ottieni tutti i file MP3 dalla memoria esterna
        val musicFiles = searchForMp3Files(Environment.getExternalStorageDirectory())

        mp3Files.clear()
        mp3Files.addAll(musicFiles)

        if (mp3Files.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Nessun file MP3 trovato",
                Toast.LENGTH_SHORT
            ).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        // Rilascia il MediaPlayer quando il fragment viene distrutto
        if (::mediaPlayerManager.isInitialized) {
            mediaPlayerManager.release()
        }
        _binding = null
    }
}