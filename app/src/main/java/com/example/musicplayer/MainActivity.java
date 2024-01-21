package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    private DatabaseManager database;
    private ExecutorService executorService;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getApplicationContext().deleteDatabase("MusicPlayerDataBase");

        recyclerView = findViewById(R.id.recycler_view);
        noMusicTextView = findViewById(R.id.no_songs_text);
        database = DatabaseManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        if(!checkPermission()){
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,null);


        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));
            if(new File(songData.getPath()).exists()){
                //songsList.add(songData);
                insert(songData);
            }
        }
        CountDownLatch latch = new CountDownLatch(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("letsee", "zmienna: " + songsList.toString());
                Log.d("letsee", "baza: " + database.audioDao().getAllAudios().size());
                songsList = new ArrayList<>(database.audioDao().getAllAudios());
                Log.d("letsee", "zmienna po wpisaniu: " + songsList.toString());
                latch.countDown();

            }
        });

        try {
            latch.await();  // Czekaj, aż licznik spadnie do zera.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        noMusicTextView = findViewById(R.id.no_songs_text);
        if(songsList.isEmpty()){
            noMusicTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            //recyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }


    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO)){
            Toast.makeText(MainActivity.this, "READ PERMISSION IS REQUIRED, PLEASE ALLOW FROM  SETTINGS", Toast.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 123);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.cancel(NotificationChannelBuilder.CHANNEL_ID_INT);
    }


    private void insert(AudioModel audioToInsert){
        final boolean[] isInserted = {false};

        executorService.execute(new Runnable() {
            @Override
            public void run() {


                for(int i = 0; i < database.audioDao().getAllAudios().size(); i++){
                    if(database.audioDao().getAllAudios().get(i).getPath().equals(audioToInsert.path))
                    {
                        isInserted[0] = true;
                    }
                }
                if(!isInserted[0])
                {
                    database.audioDao().insert(audioToInsert);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!isInserted[0]) {
                            Toast.makeText(MainActivity.this, "Audio inserted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Nie udalo sie ;(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}