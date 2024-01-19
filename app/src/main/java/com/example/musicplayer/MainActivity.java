package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    RecyclerView audioRecyclerView;
    RecyclerView playlistRecyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    private DatabaseManager database;
    private ExecutorService executorService;
    private Handler handler;
    List<AudioModel> tmpFirebaseListAudio = new ArrayList<>();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getApplicationContext().deleteDatabase("MusicPlayerDataBase");

        audioRecyclerView = findViewById(R.id.recycler_view);
        playlistRecyclerView = findViewById(R.id.playlist_recycler_view);
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
            Log.d("letsee", cursor.getString(2));
            if(new File(songData.getPath()).exists()){
                //songsList.add(songData);
                insert(songData);
            }
        }

        CountDownLatch latch = new CountDownLatch(1);

        initializeProgresBar();
        fetchLocalData(new DataCallback() {
            @Override
            public void onDataLoaded(List<AudioModel> data) {
                latch.countDown();
            }
        });

        try{
            //latch.await();
        }catch (Exception e)
        {
            Log.d("letsee", String.valueOf(e));
        }

        fetchFirebaseData(new DataCallback() {
            @Override
            public void onDataLoaded(List<AudioModel> data) {
                tmpFirebaseListAudio = data;

                songsList.addAll(tmpFirebaseListAudio);
                Log.d("letsee", "Po zczytaniu do głównej listy: " + songsList.size() + " Firebase: " + tmpFirebaseListAudio.size());

                noMusicTextView = findViewById(R.id.no_songs_text);
                if (songsList.isEmpty()) {
                    noMusicTextView.setVisibility(View.VISIBLE);
                } else {
                    audioRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    audioRecyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
                }
            }
        });

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
        if(audioRecyclerView !=null){
            audioRecyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
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
    private void fetchFirebaseData(DataCallback callback) {
        FirebaseFirestore.getInstance().collection("song").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    if (!document.isEmpty()) {
                        List<AudioModel> tmpFirebaseListAudio = document.toObjects(AudioModel.class);
                        Log.d("letsee", "DocumentSnapshot data tmpfireBase: " + tmpFirebaseListAudio.size());
                        callback.onDataLoaded(tmpFirebaseListAudio);
                    } else {
                        Log.d("letsee", "No such document");
                    }
                } else {
                    Log.d("letsee", "get failed with ", task.getException());
                }
                progressDialog.dismiss();
            }
        });
    }
    private void fetchLocalData(DataCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("letsee", "LocalData działa");

                List<AudioModel> tmpSongList = new ArrayList<>(database.audioDao().getAllAudios());
                for(int i = 0; i < tmpSongList.size(); i++)
                    songsList.add(tmpSongList.get(i));
            }

        });
    }

    private void initializeProgresBar(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading songs from Firebase...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }



}