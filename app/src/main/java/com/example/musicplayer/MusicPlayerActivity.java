package com.example.musicplayer;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaTimestamp;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity implements Observer {

    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePLay, nextBtn, previousBtn, musicIcon, strategyBtn;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    PlaybackStrategy strategy;
    int strategyId; //1 - SequencePlay, 2 - RandomPlay, 3 - LoopPlay
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    private Observable musicNotification;

    private NotificationChannelBuilder notificationChannelBuilder;
    private static final String KEY_CHOSEN_STRATEGY = "chosenStrategy";
    private SharedPreferences sharedPreferences;
    int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);


        notificationChannelBuilder = new NotificationChannelBuilder();

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePLay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        strategyBtn = findViewById(R.id.playing_strategy);
        musicIcon = findViewById(R.id.music_icon_playing);

        titleTv.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");


        setResourcesWithMusic();

        musicNotification = new MusicNotification();
        musicNotification.addObserver(this);
        createNotificationChannel();
        notificationChannelBuilder.showNotification(this, songsList.get(MyMediaPlayer.currentIndex), R.drawable.baseline_pause_circle_outline_24);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        strategyId = sharedPreferences.getInt(KEY_CHOSEN_STRATEGY, 1);
        updateStrategy();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if(mediaPlayer != null)
                {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePLay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                    }
                    else
                    {
                        pausePLay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                        x=0;
                    }
                    if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration())
                        playAfterFinish();
                }
                new Handler().postDelayed(this, 100);
            }

        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CHOSEN_STRATEGY, strategyId);
    }

    void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPlayer.currentIndex);
        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePLay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());
        strategyBtn.setOnClickListener(v -> changeStrategy());

        playMusic();
    }



    private void playMusic(){

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStrategy(){
        switch (strategyId) {
            case 1:
                strategy = new SequencePlay();
                strategyBtn.setImageResource(R.drawable.baseline_repeat_24);
                break;
            case 2:
                strategy = new RandomPlay();
                strategyBtn.setImageResource(R.drawable.baseline_shuffle_24);
                break;
            case 3:
                strategy = new LoopPlay();
                strategyBtn.setImageResource(R.drawable.baseline_repeat_one_24);
                break;
            default:
                strategy = new SequencePlay();
                strategyBtn.setImageResource(R.drawable.baseline_repeat_24);
                break;
        }
    }

    private void changeStrategy(){
        if (strategyId == 3)
            strategyId = 1;
        else
            strategyId++;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CHOSEN_STRATEGY, strategyId);
        editor.apply();
        updateStrategy();
    }

    private void playNextSong(){
        strategy.playNextSong(mediaPlayer, (songsList.size())-1);
        setResourcesWithMusic();
        notificationChannelBuilder.showNotification(this, songsList.get(MyMediaPlayer.currentIndex), R.drawable.baseline_pause_circle_outline_24);

    }

    private void playPreviousSong(){
        strategy.playPreviousSong(mediaPlayer, (songsList.size())-1);
        setResourcesWithMusic();
    }

    private void playAfterFinish(){
        strategy.playAfterFinish(mediaPlayer, (songsList.size())-1);
        setResourcesWithMusic();
        notificationChannelBuilder.showNotification(this, songsList.get(MyMediaPlayer.currentIndex), R.drawable.baseline_pause_circle_outline_24);
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            notificationChannelBuilder.showNotification(this, songsList.get(MyMediaPlayer.currentIndex), R.drawable.baseline_play_circle_outline_24);
        } else {
            mediaPlayer.start();
            notificationChannelBuilder.showNotification(this, songsList.get(MyMediaPlayer.currentIndex), R.drawable.baseline_pause_circle_outline_24);

        }
    }

    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    private void createNotificationChannel() {
        // dla API 26+
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NotificationChannelBuilder.CHANNEL_ID, NotificationChannelBuilder.CHANNEL_NAME, importance);

            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    public void updateOnPrevious() {
        playPreviousSong();
   }

    @Override
    public void updateOnNext() {
        playNextSong();

    }

    @Override
    public void updateOnPlayPause() {
        pausePlay();
    }


}