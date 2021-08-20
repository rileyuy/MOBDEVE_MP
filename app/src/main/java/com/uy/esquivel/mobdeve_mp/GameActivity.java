package com.uy.esquivel.mobdeve_mp;

import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    private int asteroid_loc;
    private int score;

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
        img.setX(65);
        img.setY(480);

        /*
        lower left x:65, y:480
        lower right x:305, y:480
        upper left x:65, y:180
        upper right x:305, y:180
         */


        Timer t = new Timer();

        TimerTask tt = new TimerTask() {
            @Override
            public void run()
            {
                TextView warning = findViewById(R.id.tv_warning);
                switch (player_state){
                    case 0:
                        gyroscope.register();
                        asteroid_loc = (int)Math.floor(Math.random()*(3-0+1)+0);
                        Log.i ("PLAYER STATE", "ASTEROID POS: " + asteroid_loc);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (asteroid_loc){
                                    case 0:
                                        warning.setText("ASTEROID INCOMING:\nTOP LEFT");
                                        break;
                                    case 1:
                                        warning.setText("ASTEROID INCOMING:\nTOP RIGHT");
                                        break;
                                    case 2:
                                        warning.setText("ASTEROID INCOMING:\nBOTTOM LEFT");
                                        break;
                                    case 3:
                                        warning.setText("ASTEROID INCOMING:\nBOTTOM RIGHT");
                                        break;
                                }
                            }
                        });
                        player_state = 1;
                        break;

                    case 1:
                        Log.i ("PLAYER STATE", state);
                        Log.i ("UNREGISTERED", "gyro disabled");
                        gyroscope.unregister();
                        switch (asteroid_loc){
                            case 0:
                                if (state.equals("upper_left"))
                                    player_state = 2;
                                else
                                    player_state = 0;
                                break;
                            case 1:
                                if (state.equals("upper_right"))
                                    player_state = 2;
                                else
                                    player_state = 0;
                                break;
                            case 2:
                                if (state.equals("lower_left"))
                                    player_state = 2;
                                else
                                    player_state = 0;
                                break;
                            case 3:
                                if (state.equals("lower_right"))
                                    player_state = 2;
                                else
                                    player_state = 0;
                                break;
                            default:
                        }
                        break;

                    case 2:
                        Log.i ("PLAYER STATE", "haha talo ./.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                warning.setText("HAHA TALO BOBO AHAAHAHAHAHAHAHAHAHAHAHAHAHAHAA");
                            }
                        });
                        break;
                    default:

                }
            }
        };

        t.schedule(tt,0,8000);

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

                states = state.split ("_");
                //Log.i ("COORDS", "rX " + rX + " rY " + rY);
                if (Math.abs(rY)>Math.abs(rX)){
                    if (rY > 0.0f){ //rotate right
                        Log.i ("ROTATION", "Rotating right! " + states[0] + states[1]);

                        if (states[0].equals("lower")){//if lower
                            if (states[1].equals("left")){
                                state = "lower_right";
                                img.setX(305);
                                img.setY(480);
                            }
                        }
                        else { //if upper
                            if (states[1].equals("left")){
                                state = "upper_right";
                                img.setX(305);
                                img.setY(180);
                            }
                        }
                    }
                    else if (rY < 0.0f){ //rotate left

                        Log.i ("ROTATION", "Rotating left! " + states[0] + states[1]);

                        if (states[0].equals("lower")){//if lower
                            if (states[1].equals("right")){
                                state = "lower_left";
                                img.setX(65);
                                img.setY(480);
                            }
                        }
                        else { //if upper
                            if (states[1].equals("right")){
                                state = "upper_left";
                                img.setX(65);
                                img.setY(180);
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
                                img.setX(65);
                                img.setY(480);
                            }
                            else{
                                state = "lower_right";
                                img.setX(305);
                                img.setY(480);
                            }
                        }
                    }
                    else if (rX < 0.0f){ //rotate upward
                        Log.i ("ROTATION", "Rotating upward! " + states[0] + states[1]);
                        if (states[0].equals("lower")){
                            if (states[1].equals("left")){
                                state = "upper_left";
                                img.setX(65);
                                img.setY(180);
                            }
                            else{
                                state = "upper_right";
                                img.setX(305);
                                img.setY(180);
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
