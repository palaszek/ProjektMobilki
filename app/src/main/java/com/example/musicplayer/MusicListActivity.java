package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity {

    TextView playlistName;
    RecyclerView songsListRecyclerView;
    ArrayList<AudioModel> songsList;
    PlaylistModel currPlaylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);


        playlistName = findViewById(R.id.playlist_title_name);
        songsListRecyclerView = findViewById(R.id.song_list_recycler_view);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("SONGSLIST");
        currPlaylist = (PlaylistModel) getIntent().getSerializableExtra("PLAYLIST");

        playlistName.setText(currPlaylist.playlistName);

        Log.d("letsee", "WIELKOSC LISTY: " + songsList.size());

        songsListRecyclerView.setLayoutManager(new LinearLayoutManager(MusicListActivity.this));
        songsListRecyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));


    }
}