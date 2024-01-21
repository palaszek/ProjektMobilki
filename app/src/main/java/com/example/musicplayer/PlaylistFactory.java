package com.example.musicplayer;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaylistFactory {
    static private DatabaseManager database = DatabaseManager.getInstance();
    static private ExecutorService executorService = Executors.newSingleThreadExecutor();
    static CountDownLatch latch = new CountDownLatch(1);
    public static PlaylistModel createPlaylist(String type){
        final PlaylistModel[] playlistModel = new PlaylistModel[1];
        switch(type){
            case "Local":
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            boolean inserted = false;
                            playlistModel[0] = new PlaylistModel("Lokalne");
                            for(int i = 0; i < database.playlistDao().getPlaylistsWithSongs().size(); i++) {
                                if (database.playlistDao().getPlaylist(i).playlistName.equals("Lokalne")) {
                                    inserted = true;
                                }
                            }
                            if(!inserted)
                                database.playlistDao().insertPlaylist(playlistModel[0]);

                            playlistModel[0].playlistId = database.playlistDao().getPlaylist("Lokalne").playlistId;
                            for(int i = 0; i < database.audioDao().getAllAudios().size();i++)
                            {
                                PlaylistSongCrossRef relaction = new PlaylistSongCrossRef();
                                relaction.songId = database.audioDao().getAudio(i+1).songId;
                                relaction.playlistId = database.playlistDao().getPlaylist("Lokalne").playlistId;
                                database.playlistDao().addSongToPlaylist(relaction);
                            }
                        } catch (Exception e)
                        {
                            Log.d("letsee", "Błąd z Fabryki: " + e);
                        }
                        latch.countDown();
                    }
                });

                break;

            case "favourite":
                break;
            default:
                playlistModel[0] = new PlaylistModel(type);
                break;

        }
        try{
            latch.await();
        }catch (Exception e){

        }
        Log.d("letsee", "Co utworzyła fabryka: " + playlistModel[0].playlistName + ", ID: " +playlistModel[0].playlistId);
        return playlistModel[0];
    }
}
