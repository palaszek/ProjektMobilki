package com.example.musicplayer;

import android.media.MediaPlayer;

public class LoopPlay implements PlaybackStrategy{
    @Override
    public void playNextSong(MediaPlayer mediaPlayer, int songsAmount)
    {
        if(MyMediaPlayer.currentIndex == songsAmount)
            MyMediaPlayer.currentIndex = 0;
        else
            MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
    }
    @Override
    public void playPreviousSong(MediaPlayer mediaPlayer, int songsAmount)
    {
        if(MyMediaPlayer.currentIndex == 0)
            MyMediaPlayer.currentIndex = songsAmount;
        else
            MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
    }
    @Override
    public void playAfterFinish(MediaPlayer mediaPlayer, int songsAmount)
    {

    }
}