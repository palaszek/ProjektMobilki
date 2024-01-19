package com.example.musicplayer;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class PlaylistWithSongs {
    @Embedded
    public PlaylistModel playlist;
    @Relation(
            parentColumn = "playlistId",
            entityColumn = "songId",
            associateBy = @Junction(PlaylistSongCrossRef.class)
    )
    public List<AudioModel> songs;
}