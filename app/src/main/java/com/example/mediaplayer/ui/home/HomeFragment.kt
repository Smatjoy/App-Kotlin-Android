package com.example.mediaplayer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.Mp3Adapter
import com.example.mediaplayer.databinding.FragmentHomeBinding
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mp3Adapter: Mp3Adapter
    private val mp3Files = ArrayList<File>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Configura il TextView
        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.textHome.text = it
        }

        // Configura la RecyclerView
        setupRecyclerView()

        return root
    }

    private fun setupRecyclerView() {
        mp3Adapter = Mp3Adapter(mp3Files) { file ->
            // Gestisci il click sul file
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mp3Adapter
        }

        // Carica i file MP3
        // loadMp3Files() // Implementa questa funzione per caricare i file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}