package com.example.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MusicNotification extends BroadcastReceiver implements  Observable {

    private List<Observer> observers;
    private ObserversSaver saver;
    private String action = "";

    public MusicNotification() {
        observers = new ArrayList();
        saver = ObserversSaver.getInstance();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String receivedAction = intent.getAction();

        observers = saver.GetObservers();

        if (receivedAction != null && receivedAction.equals("ACTION_PREVIOUS")) {
            action = "ACTION_PREVIOUS";
            Log.d("MusicNotification", "DZIALA - ACTION_PREVIOUS");

        }
        else if(receivedAction != null && receivedAction.equals("ACTION_NEXT"))
        {
            action = "ACTION_NEXT";
            Log.d("MusicNotification", "DZIALA - ACTION_NEXT");
        }
        else if(receivedAction != null && receivedAction.equals("ACTION_PLAY/PAUSE"))
        {
            action = "ACTION_PLAY/PAUSE";
            Log.d("MusicNotification", "DZIALA - ACTION_PLAY/PAUSE");
        }

        notifyObserver();
    }

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);

            saver.SetObservers(observers);
        }
    }


    @Override
    public void removeObserver(Observer observer) {
        if(!observers.isEmpty())
        {
            observers.remove(observer);
            saver.SetObservers(observers);
        }

    }

    @Override
    public void notifyObserver() {
        Log.d("MusicNotification", "DZIALA - notifyObserver - nazwa akcji: " + action);
        Log.d("MusicNotification", "DZIALA - notifyObserver - ilosc obserwatorow: " + observers.size());
        switch(action)  {
            case "ACTION_PREVIOUS":
               for(Observer observer : observers)
                    observer.updateOnPrevious();
                break;
            case "ACTION_NEXT":
                for(Observer observer : observers)
                    observer.updateOnNext();
                break;
            case "ACTION_PLAY/PAUSE":
                for(Observer observer : observers)
                    observer.updateOnPlayPause();
                break;
        }
    }



}
