package com.example.musicplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RealFirebaseService implements  IFirebaseService {
    private List<AudioModel> tmpFirebaseListAudio = new ArrayList<>();
    private ProgressDialog progressDialog;

    Context context;

    public RealFirebaseService(Context context) {
        this.context = context;
    }

    @Override
    public void fetchData(DataCallback callback) {
        initializeProgresBar();

        Log.d("RealFirebase", "start fetching data...");

        fetchFirebaseData(new DataCallback() {
            @Override
            public void onDataLoaded(List<AudioModel> data) {
                Log.d("RealFirebase", "size for data: " + data.size());

                callback.onDataLoaded(data);
            }
        });

    }


    private void initializeProgresBar(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Downloading songs from Firebase...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void fetchFirebaseData(DataCallback callback) {
        FirebaseFirestore.getInstance().collection("song").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    if (!document.isEmpty()) {
                        List<AudioModel> tmpFirebaseListAudio = document.toObjects(AudioModel.class);
                        Log.d("letsee", "DocumentSnapshot data tmpfireBase: " + tmpFirebaseListAudio.size());
                        callback.onDataLoaded(tmpFirebaseListAudio);
                    } else {
                        Log.d("letsee", "No such document");
                    }
                }
                else
                {
                    Log.d("letsee", "get failed with ", task.getException());
                }

                progressDialog.dismiss();
            }
        });
    }


}
