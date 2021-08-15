package com.uy.esquivel.mobdeve_mp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.uy.esquivel.mobdeve_mp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bStart = findViewById(R.id.b_start);

        bStart.setOnClickListener(view -> {
            Intent i = new Intent (MainActivity.this, GameActivity.class);
            startActivity (i);
        });
    }


}