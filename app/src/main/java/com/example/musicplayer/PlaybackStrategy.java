package com.example.musicplayer;

import android.media.MediaPlayer;

public interface PlaybackStrategy {
    public void playNextSong(MediaPlayer mediaPlayer, int songsAmount);
    public void playPreviousSong(MediaPlayer mediaPlayer, int songsAmount);
    public void playAfterFinish(MediaPlayer mediaPlayer, int songsAmount);
}
