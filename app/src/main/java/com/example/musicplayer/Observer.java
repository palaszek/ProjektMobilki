package com.example.musicplayer;

public interface Observer {
    public void updateOnPrevious();

    public void updateOnNext();

    public void updateOnPlayPause();
}
