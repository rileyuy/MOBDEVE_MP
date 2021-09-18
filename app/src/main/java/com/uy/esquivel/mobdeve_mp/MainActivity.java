package com.uy.esquivel.mobdeve_mp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.uy.esquivel.mobdeve_mp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    MediaPlayer player;
    private ActivityMainBinding binding;
    public static final int RECORD_AUDIO = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
        }

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