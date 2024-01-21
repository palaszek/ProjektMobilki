package com.example.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FirebaseProxyService implements  IFirebaseService {

    private RealFirebaseService realService;

    private Context context;
    ConnectivityManager connectivityManager;
    boolean isConnectedToInternet = false;

    public FirebaseProxyService(Context context, ConnectivityManager connectivityManager) {

        this.context = context;
        this.connectivityManager = connectivityManager;
    }
    @Override
    public void fetchData(DataCallback callback) {


        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnectedToInternet = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        }


        Log.d("FirebaseProxy", "Internet connection: " + isConnectedToInternet);

        if(isConnectedToInternet) {
            initializeFirebaseService();
            realService.fetchData(new DataCallback() {
                @Override
                public void onDataLoaded(List<AudioModel> data) {
                    callback.onDataLoaded(data);
                }
            });
        }
        else {
            List<AudioModel> list = new ArrayList<>();

            callback.onDataLoaded(list);
        }
    }


    public void initializeFirebaseService()
    {
        Log.d("FirebaseProxyService", "new RealFirebaseService");
        if(realService == null)
            realService = new RealFirebaseService(context);
    }
}