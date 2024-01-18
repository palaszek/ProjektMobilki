package com.example.musicplayer;

import android.provider.MediaStore;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AudioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AudioModel... audios);

    @Query("select * from audios")
    List<AudioModel> getAllAudios();

    @Query("select * from audios where id like :id")
    AudioModel getUser(int id);

    @Update
    void updateAudio(AudioModel... audio);

    @Delete
    void deleteUser(AudioModel... audio);
}
