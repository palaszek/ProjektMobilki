package com.example.musicplayer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Transaction
    @Query("SELECT * FROM playlist")
    List<PlaylistWithSongs> getPlaylistsWithSongs();

    @Transaction
    @Query("SELECT * FROM playlist WHERE playlistId = :id")
    PlaylistModel getPlaylist(int id);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPlaylist(PlaylistModel... playlist);


    @Transaction
    @Query("DELETE FROM playlistsongcrossref WHERE playlistId = :playlistId AND songId = :songId")
    void removeSongFromPlaylist(int playlistId, int songId);

    @Transaction
    @Insert
    void addSongToPlaylist(PlaylistSongCrossRef playlistSongCrossRef);

    @Transaction
    @Query("DELETE FROM playlist WHERE playlistId = :playlistId")
    void deletePlaylist(int playlistId);
}