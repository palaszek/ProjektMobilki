package com.example.musicplayer;

import android.content.Context;
import android.os.AsyncTask;
import androidx.room.Database;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {AudioModel.class}, version=1)
public abstract class DatabaseManager extends RoomDatabase {

    public abstract AudioDao audioDao();
    private static DatabaseManager instance = null;

    public static synchronized DatabaseManager getInstance(Context context) {
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DatabaseManager.class, "MusicPlayerDataBase")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }


    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };


    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        public PopulateDbAsyncTask(DatabaseManager instance){
            AudioDao audioDao = instance.audioDao();
        }
        @Override protected Void doInBackground(Void... voids) { return null;}
    }

}
