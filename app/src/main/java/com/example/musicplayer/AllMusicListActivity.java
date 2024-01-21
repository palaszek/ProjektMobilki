package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllMusicListActivity extends AppCompatActivity implements OnAddToPlaylistClickListener {

    RecyclerView songsListRecyclerView;
    ArrayList<AudioModel> songsList;
    ArrayList<PlaylistModel> playlistList;
    private ExecutorService executorService;
    DatabaseManager database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_music_list);

        songsListRecyclerView = findViewById(R.id.song_list_recycler_view);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("SONGSLIST");
        playlistList = (ArrayList<PlaylistModel>) getIntent().getSerializableExtra("PLAYLISTLIST");
        executorService = Executors.newSingleThreadExecutor();
        database = DatabaseManager.getInstance();

        Log.d("letsee", songsList.toString());
        MusicListAdapter adapter = new MusicListAdapter(songsList, this, playlistList);
        adapter.setOnAddToPlaylistClickListener(this);

        songsListRecyclerView.setLayoutManager(new LinearLayoutManager(AllMusicListActivity.this));
        songsListRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onAddToPlaylistClick(AudioModel audioModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AllMusicListActivity.this);
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
                        Log.d("letsee", "PRÓBA DODANIA PIOSENKI DO PLAYLISTY: " + relaction.toString());
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