package com.example.musicplayer;

import android.media.MediaPlayer;
import java.util.Random;


public class RandomPlay implements PlaybackStrategy{
    @Override
    public void playNextSong(MediaPlayer mediaPlayer, int songsAmount)
    {
        Random random = new Random();
        int randomNumber = random.nextInt(songsAmount + 1);
        while (randomNumber == MyMediaPlayer.currentIndex)
        {
            randomNumber = random.nextInt(songsAmount + 1);
        }
        MyMediaPlayer.currentIndex = randomNumber;
        mediaPlayer.reset();
    }
    @Override
    public void playPreviousSong(MediaPlayer mediaPlayer, int songsAmount)
    {
        Random random = new Random();
        int randomNumber = random.nextInt(songsAmount + 1);
        while (randomNumber == MyMediaPlayer.currentIndex)
        {
            randomNumber = random.nextInt(songsAmount + 1);
        }
        MyMediaPlayer.currentIndex = randomNumber;
        mediaPlayer.reset();
    }
    @Override
    public void playAfterFinish(MediaPlayer mediaPlayer, int songsAmount)
    {
        Random random = new Random();
        int randomNumber = random.nextInt(songsAmount + 1);
        while (randomNumber == MyMediaPlayer.currentIndex)
        {
            randomNumber = random.nextInt(songsAmount + 1);
        }
        MyMediaPlayer.currentIndex = randomNumber;
        mediaPlayer.reset();
    }
}