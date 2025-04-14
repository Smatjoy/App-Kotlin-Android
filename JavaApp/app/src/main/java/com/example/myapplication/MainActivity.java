package com.example.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.widget.SearchView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();
    }
    public static final int PERMISSION_REQUEST_CODE = 1;
    static ArrayList<MusicFiles> musicFiles;
    static boolean shuffleBoolean = false, repeatBoolean = false;

    static ArrayList<MusicFiles> albums = new ArrayList<>();
    // Utility function to check if the current Android version is at least a given version
    public boolean isVersionAtLeast(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

    //ask For music and storage permmission
    private void permission() {
        // Android 13+ (API 33 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[] {
                                Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.READ_MEDIA_IMAGES
                        },
                        PERMISSION_REQUEST_CODE // your request code
                );
            } else {
                // Permissions granted at install time
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(this);
                initViewPager();

            }

            // Android 6 to 12 (API 23 to 32)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[] {
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        PERMISSION_REQUEST_CODE // your request code
                );
            } else {
                // Permissions granted at install time
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(this);
                initViewPager();

            }

            // Android 5 and below — no runtime permission required
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //something
            } else {
                if (isVersionAtLeast(Build.VERSION_CODES.M)) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                            },
                            PERMISSION_REQUEST_CODE // your request code
                    );
                } else if (isVersionAtLeast(Build.VERSION_CODES.TIRAMISU)) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{
                                    Manifest.permission.READ_MEDIA_AUDIO,
                                    Manifest.permission.READ_MEDIA_IMAGES,
                            },
                            PERMISSION_REQUEST_CODE // your request code
                    );
                }
            }
        }
    }


    //Initialize viewpager
    private void initViewPager () {

        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add fragments to the adapter
        viewPagerAdapter.addFragment(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragment(new AlbumFragment(), "Albums");

        // Set the adapter to the ViewPager
        viewPager.setAdapter(viewPagerAdapter);
        // Connect the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager);
    }


    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;
        public ViewPagerAdapter (@NonNull FragmentManager fm) {
            super (fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragment (Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem (int position) {
            return fragments.get(position);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle (int position) {
            return titles.get(position);
        }
    }

    public  static  ArrayList<MusicFiles> getAllAudio (Context context) {
        ArrayList<String> duplicate = new ArrayList<>();
        Log.e("Inside", "getAllAudio: ");
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, // for path
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID

        };
        String selection = MediaStore.Audio.Media.DATA + " LIKE ?";
        //Warning Edit this for Music Filtering!!!
        String[] selectionArgs = new String[]{"%/Music/%"};
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            Log.e("Cursor Check", "Count: " + cursor.getCount());
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration, id);
                //Log.e For check
                Log.e("Path: "+path, "Album: "+album);
                tempAudioList.add(musicFiles);
                if (!duplicate.contains(album)) {
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return tempAudioList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<MusicFiles> myFiles = new ArrayList<>();
        for (MusicFiles song : musicFiles) {
            if (song.getTitle().toLowerCase().contains(userInput)) {
                myFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateList(myFiles);
        return true;
    }
}
