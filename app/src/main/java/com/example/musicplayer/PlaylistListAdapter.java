package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaylistListAdapter extends RecyclerView.Adapter<PlaylistListAdapter.ViewHolder>{

    Context context;
    ArrayList<PlaylistModel> playlistList;
    Handler handler;
    DatabaseManager database;
    ArrayList<AudioModel> songToView;
    private ExecutorService executorService;

    public PlaylistListAdapter(ArrayList<PlaylistModel> playlistList, Context context)
    {
        this.playlistList = playlistList;
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
        database = DatabaseManager.getInstance();
        executorService = Executors.newSingleThreadExecutor();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_playlist_item,parent,false);
        return new PlaylistListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistListAdapter.ViewHolder holder, int position) {
        PlaylistModel playlistModel= playlistList.get(position);
        Log.d("letsee", "PlayListAdapter2, Wielkość listy: " + playlistList.size());
        holder.titleTextView.setText(playlistModel.playlistName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("letsee", "TU DZIALA"+ database.playlistDao().getPlaylistsWithSongs().size());
                        songToView = new ArrayList<>();
                        for(int i = 0; i < database.playlistDao().getPlaylistsWithSongs().size();i++)
                        {
                            if(playlistList.get(holder.getAdapterPosition()).playlistId == database.playlistDao().getPlaylistsWithSongs().get(i).playlist.playlistId)
                            {
                                songToView = new ArrayList<>(database.playlistDao().getPlaylistsWithSongs().get(i).songs);
                            }
                        }
                        Intent intent = new Intent(context, MusicListActivity.class);

                        intent.putExtra("SONGSLIST", songToView);
                        intent.putExtra("PLAYLIST", playlistList.get(holder.getAdapterPosition()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try{
                            context.startActivity(intent);
                        }catch (Exception e)
                        {
                            Log.d("letsee","BŁĄD INTENT: " + e);
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        ImageView iconImageView;

        public ViewHolder(View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.playlist_title_text);
            iconImageView = itemView.findViewById(R.id.cover_image_playlist);
        }
    }
}
