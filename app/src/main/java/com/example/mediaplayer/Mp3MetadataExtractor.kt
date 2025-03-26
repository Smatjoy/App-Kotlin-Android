package com.example.mediaplayer

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File

object Mp3MetadataExtractor {

    fun extractMetadata(context: Context, file: File): Song {
        val retriever = MediaMetadataRetriever()
        val fileUri = Uri.fromFile(file)

        return try {
            retriever.setDataSource(context, fileUri)

            // Extract basic metadata
            val id = file.hashCode().toLong()
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.name
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
            val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown Album"

            // Extract album art
            val albumArtBytes = retriever.embeddedPicture
            val albumArt = if (albumArtBytes != null) {
                BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.size)
            } else {
                null
            }

            Song(id, title, artist, album, albumArt, fileUri)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback for when metadata extraction fails
            Song(
                file.hashCode().toLong(),
                file.name,
                "Unknown Artist",
                "Unknown Album",
                null,
                fileUri
            )
        } finally {
            retriever.release()
        }
    }
}