package com.uy.esquivel.mobdeve_mp;

import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;

import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.uy.esquivel.mobdeve_mp.databinding.ActivityGameBinding;

import pl.droidsonroids.gif.GifImageView;


public class GameActivity extends AppCompatActivity {

    MediaPlayer player;
    private ActivityGameBinding binding;
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;

    //game-related variables
    private String state;
    private int asteroid_loc;
    private int score;
    private int hasEnded = 0;

    /*
        player_state = 0; player can move freely, after a set amount of time, player_state becomes 1
        player_state = 1; asteroid sequence plays, if player successfully dodges asteroid, player_state goes back to 0
        player_state = 2; player is dead
     */
    private int player_state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);

        state = "lower_left";
        final ImageView img = findViewById(R.id.iv_ship);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        if (width == 480 && height == 854){
            img.setX(65);
            img.setY(480);
        }else{
            img.setX(640);
            img.setY(450);
        }

        /*
        lower left x:65, y:480
        lower right x:305, y:480
        upper left x:65, y:180
        upper right x:305, y:180
         */

        /*
        lower left x:105, y:1180
        lower right x:640, y:1180
        upper left x:105, y:450
        upper right x:640, y:450
         */


        Timer t = new Timer();

        TimerTask tt = new TimerTask() {
            @Override
            public void run()
            {
                ImageView grid = findViewById(R.id.iv_grid);
                VideoView asteroid = findViewById(R.id.vv_asteroid);
                GifImageView giv = findViewById(R.id.giv_spacebg);
                ImageView shp = findViewById(R.id.iv_ship);
                Button playagain = findViewById(R.id.b_playagain);

                switch (player_state){
                    case 0:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playagain.setVisibility(View.GONE);
                                shp.setVisibility(View.VISIBLE);
                                grid.setVisibility(View.VISIBLE);
                                giv.setImageResource(R.drawable.spacebg);
                                giv.setVisibility(View.VISIBLE);

                            }
                        });
                        gyroscope.register();
                        asteroid_loc = (int)Math.floor(Math.random()*(3-0+1)+0);
                        Log.i ("PLAYER STATE", "ASTEROID POS: " + asteroid_loc);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (asteroid_loc){
                                    case 0:
                                        grid.setImageResource(R.drawable.grid_up_left);
                                        break;
                                    case 1:
                                        grid.setImageResource(R.drawable.grid_up_right);
                                        break;
                                    case 2:
                                        grid.setImageResource(R.drawable.grid_down_left);
                                        break;
                                    case 3:
                                        grid.setImageResource(R.drawable.grid_down_right);
                                        break;
                                }
                            }
                        });
                        player_state = 1;
                        break;

                    case 1:
                        Log.i ("PLAYER STATE", state);
                        Log.i ("UNREGISTERED", "gyro disabled");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                giv.setVisibility(View.VISIBLE);
                                shp.setVisibility(View.VISIBLE);
                                asteroid.setVisibility(View.VISIBLE);
                            }
                        });
                        gyroscope.unregister();
                        switch (asteroid_loc){
                            case 0:
                                if (state.equals("upper_left"))
                                    player_state = 2;
                                else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            giv.setVisibility(View.GONE);
                                        }
                                    });
                                    Uri uri;
                                    switch (state){
                                        case "lower_left":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_up);
                                            break;
                                        case "lower_right":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_upper_left);
                                            break;
                                        case "upper_right":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_left);
                                            break;
                                        default:
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_game_over);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            grid.setVisibility(View.GONE);
                                            shp.setVisibility(View.GONE);
                                            asteroid.setVideoURI(uri);
                                            asteroid.start();
                                        }
                                    });
                                    asteroid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    asteroid.setVisibility(View.GONE);
                                                    giv.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    });
                                    player_state = 0;
                                }

                                break;
                            case 1:
                                if (state.equals("upper_right"))
                                    player_state = 2;
                                else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            giv.setVisibility(View.GONE);
                                        }
                                    });

                                    Uri uri;
                                    switch (state){
                                        case "upper_left":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_right);
                                            break;
                                        case "lower_right":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_up);
                                            break;
                                        case "lower_left":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_upper_right);
                                            break;
                                        default:
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_game_over);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            shp.setVisibility(View.GONE);
                                            grid.setVisibility(View.GONE);
                                            asteroid.setVideoURI(uri);
                                            asteroid.start();
                                        }
                                    });

                                    asteroid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    asteroid.setVisibility(View.GONE);
                                                    giv.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    });
                                    player_state = 0;
                                }

                                break;
                            case 2:
                                if (state.equals("lower_left"))
                                    player_state = 2;
                                else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            giv.setVisibility(View.GONE);
                                        }
                                    });
                                    Uri uri;
                                    switch (state){
                                        case "upper_left":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_down);
                                            break;
                                        case "lower_right":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_left);
                                            break;
                                        case "upper_right":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_lower_left);
                                            break;
                                        default:
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_game_over);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            shp.setVisibility(View.GONE);
                                            grid.setVisibility(View.GONE);
                                            asteroid.setVideoURI(uri);
                                            asteroid.start();
                                        }
                                    });
                                    asteroid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    asteroid.setVisibility(View.GONE);
                                                    giv.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    });
                                    player_state = 0;
                                }

                                break;
                            case 3:
                                if (state.equals("lower_right"))
                                    player_state = 2;
                                else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            giv.setVisibility(View.GONE);
                                        }
                                    });
                                    Uri uri;
                                    switch (state){
                                        case "lower_left":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_right);
                                            break;
                                        case "upper_left":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_lower_right);
                                            break;
                                        case "upper_right":
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_down);
                                            break;
                                        default:
                                            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_game_over);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            shp.setVisibility(View.GONE);
                                            grid.setVisibility(View.GONE);
                                            asteroid.setVideoURI(uri);
                                            asteroid.start();
                                        }
                                    });
                                    asteroid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    asteroid.setVisibility(View.GONE);
                                                    giv.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    });
                                    player_state = 0;
                                }

                                break;
                            default:
                        }
                        break;

                    case 2:
                        Log.i ("PLAYER STATE", "YOU LOSE!");
                        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_and_explosion);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                grid.setVisibility(View.GONE);
                                shp.setVisibility(View.GONE);
                                if (hasEnded == 0)
                                    giv.setVisibility(View.GONE);
                                asteroid.setVideoURI(uri);
                                asteroid.start();
                            }
                        });

                        asteroid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        giv.setImageResource(R.drawable.game_over_loop);
                                        giv.setVisibility(View.VISIBLE);
                                        playagain.setVisibility(View.VISIBLE);
                                        asteroid.setVisibility(View.GONE);
                                        stopPlayer();
                                        playEnd(view);
                                        hasEnded = 1;
                                    }
                                });
                            }
                        });

                        playagain.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                hasEnded = 0;
                                player_state = 0;
                            }
                        });


                        break;
                    default:

                }
            }
        };

        t.schedule(tt,0,5000);

        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rX, float rY, float rZ) {
                String states[] = new String[2];

                /*
                    rotate right = positive rY
                    rotate left = negative rY

                    rotate upward = negative rX
                    rotate downward = positive rX
                 */

                /*
                    HUAWEI P30 Lite reso coords
                    lower left x:105, y:1180
                    lower right x:640, y:1180
                    upper left x:105, y:450
                    upper right x:640, y:450
                */

                states = state.split ("_");
                //Log.i ("COORDS", "rX " + rX + " rY " + rY);
                if (Math.abs(rY)>Math.abs(rX)){
                    if (rY > 0.0f){ //rotate right
                        Log.i ("ROTATION", "Rotating right! " + states[0] + states[1]);

                        if (states[0].equals("lower")){//if lower
                            if (states[1].equals("left")){
                                state = "lower_right";
                                if (width == 480 && height == 854){
                                    img.setX(305);
                                    img.setY(480);
                                }else{
                                    img.setX(640);
                                    img.setY(1180);
                                }

                            }
                        }
                        else { //if upper
                            if (states[1].equals("left")){
                                state = "upper_right";
                                if (width == 480 && height == 854){
                                    img.setX(305);
                                    img.setY(180);
                                }
                                else{
                                    img.setX(640);
                                    img.setY(450);
                                }

                            }
                        }
                    }
                    else if (rY < 0.0f){ //rotate left

                        Log.i ("ROTATION", "Rotating left! " + states[0] + states[1]);

                        if (states[0].equals("lower")){//if lower
                            if (states[1].equals("right")){
                                state = "lower_left";
                                if (width == 480 && height == 854){
                                    img.setX(65);
                                    img.setY(480);
                                }
                                else{
                                    img.setX(105);
                                    img.setY(1180);
                                }

                            }
                        }
                        else { //if upper
                            if (states[1].equals("right")){
                                state = "upper_left";
                                if (width == 480 && height == 854){
                                    img.setX(65);
                                    img.setY(180);
                                }
                                else{
                                    img.setX(105);
                                    img.setY(450);
                                }

                            }
                        }
                    }
                }
                else{
                    if (rX > 0.0f){ //rotate downward
                        Log.i ("ROTATION", "Rotating downward! " + states[0] + states[1]);
                        if (states[0].equals("upper")){
                            if (states[1].equals("left")){
                                state = "lower_left";
                                if (width == 480 && height == 854){
                                    img.setX(65);
                                    img.setY(480);
                                }
                                else{
                                    img.setX(105);
                                    img.setY(1180);
                                }

                            }
                            else{
                                state = "lower_right";
                                if (width == 480 && height == 854){
                                    img.setX(305);
                                    img.setY(480);
                                }
                                else{
                                    img.setX(640);
                                    img.setY(1180);
                                }

                            }
                        }
                    }
                    else if (rX < 0.0f){ //rotate upward
                        Log.i ("ROTATION", "Rotating upward! " + states[0] + states[1]);
                        if (states[0].equals("lower")){
                            if (states[1].equals("left")){
                                state = "upper_left";
                                if (width == 480 && height == 854){
                                    img.setX(65);
                                    img.setY(180);
                                }
                                else{
                                    img.setX(105);
                                    img.setY(450);
                                }

                            }
                            else{
                                state = "upper_right";
                                if (width == 480 && height == 854){
                                    img.setX(305);
                                    img.setY(180);
                                }
                                else{
                                    img.setX(640);
                                    img.setY(450);
                                }

                            }
                        }
                    }
                }
            }
        });
        play(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gyroscope.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gyroscope.unregister();
        stopPlayer();
    }

    public void play(View v) {
        player = MediaPlayer.create(this, R.raw.ingamesong);
        player.setLooping(true);
        player.start();
    }

    public void playEnd(View v) {
        player = MediaPlayer.create(this, R.raw.gameover);
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

    @Override
    public void onBackPressed() {
    }
}
