package com.example.myapplication;

import static com.example.myapplication.MainActivity.musicFiles;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    TextView album_name;
    String albumName;

    //Se il grena ha modificato questo modificate
    ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.recyclerView);
        albumPhoto = findViewById(R.id.albumPhoto);
        albumName = getIntent().getStringExtra("albumName");
        int j = 0;
        for (int i = 0; i < musicFiles.size(); i++) {
            if (albumName.equals(musicFiles.get(i).getAlbum())) {
                albumSongs.add(j, musicFiles.get(i));
                j++;
            }
        }

        for (int i = 0; i < albumSongs.size(); i++) {
            //Log.e("albumSongs", albumSongs.get(i).getTitle());
        }

        album_name = findViewById(R.id.album_name);
        if (!albumSongs.isEmpty()) {
            album_name.setText(albumSongs.get(0).getAlbum());
        } else {
            album_name.setText("Unknown Album");
        }




        byte[] image;
        try {
            image = getAlbumArt(albumSongs.get(0).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (image != null) {
            Glide.with(this)
                    .load(image)
                    .into(albumPhoto);
        } else {
            Glide.with(this)
                    .load(R.drawable.static_music)
                    .into(albumPhoto);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(true) {
        //if (albumSongs.size() > 1) {
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource( uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
