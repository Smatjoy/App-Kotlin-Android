package com.example.myapplication;

import static com.example.myapplication.MainActivity.musicFiles;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image = null;
        try {
            image = getAlbumArt(mFiles.get(position).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (image != null) {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        } else {
            MusicFiles musicFiles = mFiles.get(position);
            String imagePath = musicFiles.getPath().replaceAll("\\.(mp3|wav|m4a)$", "");
            File imageFile = new File(imagePath );
            File PngFile = new File(imagePath + ".png");
            File JpgFile = new File(imagePath + ".jpg");
            if (imageFile.exists()) {
                Glide.with(mContext)
                        .load(imageFile)
                        .override(300, 300)
                        .into(holder.album_art);
            } else {
                // Fallback to default drawable
                Glide.with(mContext)
                        .load(R.drawable.static_music)
                        .into(holder.album_art);
            }
            }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        TextView file_name;
        ImageView album_art;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
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
