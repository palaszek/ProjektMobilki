package com.example.musicplayer;

import android.media.MediaPlayer;

import java.util.List;

public class ObserversSaver {
    static ObserversSaver instance;

    public static ObserversSaver getInstance(){
        if(instance == null){
            instance = new ObserversSaver();
        }
        return instance;
    }

    private List<Observer> observers;

    public void SetObservers(List<Observer>  observers){
        this.observers = observers;
    }

    public List<Observer> GetObservers() {
        return this.observers;
    }
}
