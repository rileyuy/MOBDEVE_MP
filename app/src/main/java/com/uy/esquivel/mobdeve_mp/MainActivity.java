package com.uy.esquivel.mobdeve_mp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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
        pause(binding.getRoot());
    }


    public void play(View v) {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.menusong);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }

        player.start();
    }

    public void pause(View v) {
        if (player != null) {
            player.pause();
        }
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

}