package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicListActivity extends AppCompatActivity implements OnAddToPlaylistClickListener {

    TextView playlistName;
    RecyclerView songsListRecyclerView;
    ArrayList<AudioModel> songsList;
    PlaylistModel currPlaylist;
    ArrayList<PlaylistModel> playlistList;
    private ExecutorService executorService;
    DatabaseManager database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);


        playlistName = findViewById(R.id.playlist_title_name);
        songsListRecyclerView = findViewById(R.id.song_list_recycler_view);
        executorService = Executors.newSingleThreadExecutor();
        database = DatabaseManager.getInstance();

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("SONGSLIST");
        currPlaylist = (PlaylistModel) getIntent().getSerializableExtra("PLAYLIST");
        playlistList = (ArrayList<PlaylistModel>) getIntent().getSerializableExtra("PLAYLISTLIST");

        playlistName.setText(currPlaylist.playlistName);

        Log.d("letsee", "WIELKOSC LISTY: " + songsList.size());

        MusicListAdapter adapter = new MusicListAdapter(songsList, this, playlistList);
        adapter.setOnAddToPlaylistClickListener(this);

        songsListRecyclerView.setLayoutManager(new LinearLayoutManager(MusicListActivity.this));
        songsListRecyclerView.setAdapter(adapter);


    }

    @Override
    public void onAddToPlaylistClick(AudioModel audioModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MusicListActivity.this);
        builder.setTitle("Dodaj do Playlisty");

        // Pobieramy listę nazw playlist
        List<String> playlistNames = new ArrayList<>();
        for (PlaylistModel playlist : playlistList) {
            if(playlist.playlistId != 1)
                playlistNames.add(playlist.playlistName);
        }

        // Konwertujemy listę na tablicę dla ArrayAdapter
        String[] playlistArray = playlistNames.toArray(new String[0]);

        builder.setItems(playlistArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Dodajemy wybraną piosenkę do wybranej playlisty
                String selectedPlaylistName = playlistArray[i];
                addSongToPlaylist(audioModel, selectedPlaylistName);
            }
        });

        builder.show();
    }

    private void addSongToPlaylist(AudioModel audioModel, String playlistName) {
        // Tutaj dodaj kod do obsługi dodawania piosenki do playlisty
        // np. znalezienie odpowiedniej playlisty, dodanie piosenki i odświeżenie widoku
        for (PlaylistModel playlist : playlistList) {
            if (playlist.playlistName.equals(playlistName)) {

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        PlaylistSongCrossRef relaction = new PlaylistSongCrossRef();
                        relaction.playlistId = playlist.playlistId;
                        relaction.songId = audioModel.songId;
                        database.playlistDao().addSongToPlaylist(relaction);
                    }
                });
                //playlist.addSong(audioModel);
                //playlistRecyclerView.getAdapter().notifyDataSetChanged();
                break;
            }
        }
    }
}