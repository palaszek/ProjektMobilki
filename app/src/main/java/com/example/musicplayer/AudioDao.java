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
    void insert(AudioModel... audio);

    @Query("select * from audio")
    List<AudioModel> getAllAudios();

    @Query("select * from audio where songId like :id")
    AudioModel getAudio(int id);

    @Update
    void updateAudio(AudioModel... audio);

    @Delete
    void deleteAudio(AudioModel... audio);

    @Query("Delete from audio")
    void deleteAllAudios();
}
