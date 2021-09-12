package com.uy.esquivel.mobdeve_mp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.uy.esquivel.mobdeve_mp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    MediaPlayer player;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ImageButton bStart = findViewById(R.id.b_start);

        bStart.setOnClickListener(newView -> {
            Intent i = new Intent (MainActivity.this, GameActivity.class);
            startActivity (i);
            stopPlayer();
        });

        play(view);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        play(binding.getRoot());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayer();
    }

    public void play(View v) {
        player = MediaPlayer.create(this, R.raw.menusong);
        player.setLooping(true);
        player.start();
    }
    
    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            //Toast.makeText(this, "MediaPlayer terminated", Toast.LENGTH_SHORT).show();
        }
    }


}