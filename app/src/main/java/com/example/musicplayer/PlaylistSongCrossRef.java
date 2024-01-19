package com.example.musicplayer;

import androidx.room.Entity;

@Entity(primaryKeys = {"playlistId", "songId"}, tableName = "playlistsongcrossref")
public class PlaylistSongCrossRef {
    public int playlistId;
    public int songId;
}
