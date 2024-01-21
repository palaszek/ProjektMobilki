package com.example.musicplayer;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.util.List;

public class MyMediaPlayer {
     static android.media.MediaPlayer instance;

    public static MediaPlayer getInstance(){
        if(instance == null){
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static int currentIndex = -1;

}
