package com.example.mediaplayer

import android.graphics.Bitmap
import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumArt: Bitmap?,
    val uri: Uri
)