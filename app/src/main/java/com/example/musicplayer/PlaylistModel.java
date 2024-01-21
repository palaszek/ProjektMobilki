package com.example.musicplayer;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "playlist")
public class PlaylistModel implements Serializable{

    @PrimaryKey(autoGenerate = true)
    public int playlistId;

    public String playlistName;

    public PlaylistModel(String name){
        this.playlistName = name;
    }
    public PlaylistModel(){
        this.playlistName = "";
    }

}
