package com.example.mediaplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // MediaPlayerManager a livello di Activity, accessibile dai fragment
    lateinit var mediaPlayerManager: MediaPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inizializza il MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager(this)

        // Configura la bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Imposta Home come fragment predefinito
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_playlist -> {
                    loadFragment(PlaylistFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_player -> {
                    loadFragment(PlayerFragment())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Rilascia le risorse del MediaPlayer quando l'attività viene distrutta
        mediaPlayerManager.release()
    }
}