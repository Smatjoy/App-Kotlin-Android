package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.myapplication.ApplicationClass.ACTION_NEXT;
import static com.example.myapplication.ApplicationClass.ACTION_PLAY;
import static com.example.myapplication.ApplicationClass.ACTION_PREVIOUS;
import static com.example.myapplication.ApplicationClass.CHANNEL_ID_2;
import static com.example.myapplication.ApplicationClass.SHUFFLE_ON;
import static com.example.myapplication.PlayerActivity.listSongs;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList <MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");


        // Set the callback for the media session (play/pause functionality)
        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                    showNotification(R.drawable.ic_pause);

                    handlePlaybackStateChanges(PlaybackStateCompat.STATE_PLAYING);
                }
            }

            @Override
            public void onPause() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                    showNotification(R.drawable.ic_play);
                    handlePlaybackStateChanges(PlaybackStateCompat.STATE_PLAYING);
                }
            }

            @Override

            public void onStop() {
                stopSelf(); // Stop the service when media stops
            }
        });
        mediaSessionCompat.setActive(true); // Set the media session as active

    }

    private void updatePlaybackState(int state) {
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setState(state, mediaPlayer.getCurrentPosition(), 1.0f);
        mediaSessionCompat.setPlaybackState(stateBuilder.build());
}


    @Nullable
    @Override
    public IBinder onBind (Intent intent) {
        Log.e("Bind", "Method");
        //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        return mBinder;
    }

    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");
        if (myPosition != -1) {
            playMedia(myPosition);
        }
        if (actionName != null) {
            switch (actionName) {
                case "playPause":
                    Toast.makeText(this, "PlayPause", Toast.LENGTH_SHORT).show();
                    playPauseBtnClicked();
                    break;
                case "next":
                    Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
                    nextBtnClicked();
                    break;
                case "previous":
                    Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
                    prevBtnClicked();
                    break;
            }
        }
        return START_STICKY;
    }
    private void playMedia(int StartPosition) {
        musicFiles = listSongs;
        position = StartPosition;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (musicFiles != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else {
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start() {
        mediaPlayer.start();
    }
    boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    void stop() {
        mediaPlayer.stop();
    }
    void release() {
        mediaPlayer.release();
    }
    int getDuration() {
        return mediaPlayer.getDuration();
    }
    void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }
    int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    void createMediaPlayer(int positionInner) {
        position = positionInner;
        uri = Uri.parse(musicFiles.get(position).getPath());
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE).edit();
        editor.putString(MUSIC_FILE, uri.toString());
        editor.putString(ARTIST_NAME, musicFiles.get(position).getArtist());
        editor.putString(SONG_NAME, musicFiles.get(position).getTitle());
        editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);

    }
    void pause() {
        mediaPlayer.pause();
    }
    void OnCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlaying != null) {
            actionPlaying.nextBtnClicked();
            if (mediaPlayer != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
                OnCompleted();
                handlePlaybackStateChanges(PlaybackStateCompat.STATE_PLAYING);
            }
        } else {
            handlePlaybackStateChanges(PlaybackStateCompat.STATE_STOPPED);
        }
    }
    void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }


    /*void showNotification(int playPauseBtn) {

        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE);
        //Prev Button
        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent,
                PendingIntent.FLAG_MUTABLE);
        //Pause btn
        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent,
                PendingIntent.FLAG_MUTABLE);
        //Next btn
        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent,
                PendingIntent.FLAG_MUTABLE);

        byte[] picture = null;
        Bitmap thumb = null;
        try {
            // --- Try to get the album art ---
            picture = getAlbumArt(musicFiles.get(position).getPath());
            if (picture != null) {
                thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            } else {
                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.static_music);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Use a default image if art retrieval fails
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.static_music);
        } catch (IllegalArgumentException e) {
            // --- Handle potential IllegalArgumentException from setDataSource ---
            e.printStackTrace();
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.static_music);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .addAction(R.drawable.ic_skip_previous, "Previous", prevPending)
                //.addAction(android.R.drawable.ic_media_pause, "Pause", pausePending)
                .addAction(R.drawable.ic_pause, "playPause", pausePending)
                .addAction(R.drawable.ic_skip_next, "Next", nextPending)

                //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                //.setMediaSession(mediaSessionCompat.getSessionToken()))
                        //.setShowActionsInCompactView(0, 1, 2)

                .setStyle(new MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))

                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
        //L'indiano qua ha altre cose che però richiedono troppo da implementare ed funziona uguale
    }*/


    @SuppressLint("ForegroundServiceType")
    void showNotification(int playPauseBtn) {
        // Intent to open PlayerActivity when notification is tapped
        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        // Previous Button Intent
        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_MUTABLE);

        // Pause Button Intent
        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_MUTABLE);

        // Shuffle Button Intent
        Intent shuffleIntent = new Intent(this, NotificationReceiver.class)
                .setAction(SHUFFLE_ON);
        PendingIntent shufflePending = PendingIntent.getBroadcast(this, 0, shuffleIntent, PendingIntent.FLAG_MUTABLE);

        // Next Button Intent
        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_MUTABLE);

        // Attempt to get the album art
        byte[] picture = null;
        Bitmap thumb = null;
        try {
            picture = getAlbumArt(musicFiles.get(position).getPath());
            if (picture != null) {
                thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            } else {
                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.static_music);
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.static_music);
        }

        // Build the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .setContentIntent(contentIntent) // Open PlayerActivity when tapped
                .addAction(R.drawable.ic_repeat_off, "Shuffle", shufflePending)
                .addAction(R.drawable.ic_skip_previous, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.ic_skip_next, "Next", nextPending)
                // Add MediaStyle to link to MediaSession
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken())  // Connect to MediaSession
                        .setShowActionsInCompactView(0, 1, 2))  // Show play/pause, previous, next buttons in compact view
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        // Show the notification as a foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(1, notification);
        }
        // Manage the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }



    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource( uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    void playPauseBtnClicked() {
        if (actionPlaying != null) {
            actionPlaying.playPauseBtnClicked();
        }
    }

    void nextBtnClicked() {
        if (actionPlaying != null) {
            actionPlaying.nextBtnClicked();
        }
    }

    void prevBtnClicked() {
        if (actionPlaying != null) {
            actionPlaying.prevBtnClicked();
        }
    }

    private void handlePlaybackStateChanges(int state) {
        switch (state) {
            case PlaybackStateCompat.STATE_PLAYING:
                updatePlaybackState(state);
                showNotification(R.drawable.ic_pause);
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                updatePlaybackState(state);
                showNotification(R.drawable.ic_play);
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                updatePlaybackState(state);
                stopForeground(true);
                break;
        }
    }

}
