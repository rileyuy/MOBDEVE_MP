package com.uy.esquivel.mobdeve_mp;

import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.uy.esquivel.mobdeve_mp.databinding.ActivityGameBinding;


public class GameActivity extends AppCompatActivity {

    MediaPlayer player;
    private ActivityGameBinding binding;
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;

    //game-related variables
    private String state;
    private String asteroid_loc;
    private int score;

    /*
        player_state = 0; player can move freely, after a set amount of time, player_state becomes 1
        player_state = 1; asteroid sequence plays, if player successfully dodges asteroid, player_state goes back to 0
        player_state = 2; player is dead
     */
    private int player_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        state = "lower_left";

        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);

        Timer t = new Timer();

        TimerTask tt = new TimerTask() {
            @Override
            public void run()
            {

            }
        };

        t.schedule(tt,0,5000);

//        accelerometer.setListener(new Accelerometer.Listener() {
//            @Override
//            public void onTranslation(float tX, float tY, float tZ) {
//                ImageView img = (ImageView) findViewById(R.id.iv_bg);
//                String states[] = new String[2];
//                states = state.split ("_");
//                if (tX > 1.0f){ //going right
//
//                    if (states[0] == "lower"){//if lower
//                        if (states[1] == "left"){
//                            state = "lower_right";
//                            img.setImageResource(R.drawable.lowerright);
//                        }
//                        else{
//                            state = "upper_right";
//                            img.setImageResource(R.drawable.upperright);
//                        }
//
//                    }
//                    else { //if upper
//                        if (states[1] == "left"){
//                            state = "upper_right";
//                            img.setImageResource(R.drawable.upperright);
//                        }
//                        else{
//                            state = "upper_right";
//                            img.setImageResource(R.drawable.upperright);
//                        }
//                    }
//               }
//               else if (tX < -1.0f){ //going left
//                   if (states[0] == "lower"){ //if lower
//                       if (states[1] == "right"){
//                           state = "lower_left";
//                           img.setImageResource(R.drawable.lowerleft);
//                       }
//
//                   }
//                   else{ //if upper
//                       if (states[1] == "right"){
//                           state = "upper_left";
//                           img.setImageResource(R.drawable.upperleft);
//                       }
//                   }
//
//               }
//            }
//        });

        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rX, float rY, float rZ) {
                ImageView img = findViewById(R.id.iv_bg);
                String states[] = new String[2];

                /*
                    rotate right = positive rY
                    rotate left = negative rY

                    rotate upward = negative rX
                    rotate downward = positive rX
                 */

                states = state.split ("_");
                Log.i ("COORDS", "rX " + rX + " rY " + rY);
                if (Math.abs(rY)>Math.abs(rX)){
                    if (rY > 0.0f){ //rotate right
                        Log.i ("ROTATION", "Rotating right! " + states[0] + states[1]);

                        if (states[0].equals("lower")){//if lower
                            if (states[1].equals("left")){
                                state = "lower_right";
                                img.setImageResource(R.drawable.lowerright);
                            }
                        }
                        else { //if upper
                            if (states[1].equals("left")){
                                state = "upper_right";
                                img.setImageResource(R.drawable.upperright);
                            }
                        }
                    }
                    else if (rY < 0.0f){ //rotate left

                        Log.i ("ROTATION", "Rotating left! " + states[0] + states[1]);

                        if (states[0].equals("lower")){//if lower
                            if (states[1].equals("right")){
                                state = "lower_left";
                                img.setImageResource(R.drawable.lowerleft);
                            }
                        }
                        else { //if upper
                            if (states[1].equals("right")){
                                state = "upper_left";
                                img.setImageResource(R.drawable.upperleft);
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
                                img.setImageResource(R.drawable.lowerleft);
                            }
                            else{
                                state = "lower_right";
                                img.setImageResource(R.drawable.lowerright);
                            }
                        }
                    }
                    else if (rX < 0.0f){ //rotate upward
                        Log.i ("ROTATION", "Rotating upward! " + states[0] + states[1]);
                        if (states[0].equals("lower")){
                            if (states[1].equals("left")){
                                state = "upper_left";
                                img.setImageResource(R.drawable.upperleft);
                            }
                            else{
                                state = "upper_right";
                                img.setImageResource(R.drawable.upperright);
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
        accelerometer.register();
        gyroscope.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.unregister();
        gyroscope.unregister();
        stopPlayer();
    }

    public void play(View v) {
        player = MediaPlayer.create(this, R.raw.ingamesong);
        player.setLooping(true);
        player.start();
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            Toast.makeText(this, "MediaPlayer terminated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
