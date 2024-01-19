package com.example.musicplayer;

import java.io.Serial;
import java.io.Serializable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "audios")
public class AudioModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
            public int id;
    String path;
    String title;
    String duration;

    public AudioModel(String path, String title, String duration) {
        this.path = path;
        this.title = title;
        this.duration = duration;
    }

    public AudioModel(){
        path="";
        title="";
        duration="";
    }

    public int getId() {return id;}
    public void setId(int id){this.id = id;}
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
