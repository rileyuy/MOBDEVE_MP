package com.uy.esquivel.mobdeve_mp;

import java.lang.Math;

import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uy.esquivel.mobdeve_mp.adapter.ScoreAdapter;
import com.uy.esquivel.mobdeve_mp.dao.ScoreDAO;
import com.uy.esquivel.mobdeve_mp.dao.ScoreDAOFirebaseImpl;
import com.uy.esquivel.mobdeve_mp.databinding.ActivityGameBinding;
import com.uy.esquivel.mobdeve_mp.model.Score;

import pl.droidsonroids.gif.GifImageView;


public class GameActivity extends AppCompatActivity {

    private ScoreAdapter scoreAdapter;
    MediaPlayer player;
    private ActivityGameBinding binding;
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;

    TextView mStatusView;
    MediaRecorder mRecorder;
    Thread runner;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
    public static final int RECORD_AUDIO = 0;

    final Runnable updater = new Runnable(){
        public void run(){
            updateTv();
        };
    };

    final Handler mHandler = new Handler();

    //game-related variables
    private String state;
    private int asteroid_loc;
    private int score = 0;
    private int hasEnded = 0;

    private int powers = 3;
    private boolean powerActivate = false;

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
        mStatusView = (TextView)findViewById(R.id.tv_showDecibels);
        ImageView grid = findViewById(R.id.iv_grid);
        ImageView shp = findViewById(R.id.iv_ship);
        ImageView spacebg = findViewById(R.id.iv_spacebg);
        ImageView hand = findViewById(R.id.iv_hand);

        VideoView asteroid = findViewById(R.id.vv_asteroid);

        TextView showScore = findViewById(R.id.tv_showScore);

        //GifImageView giv = findViewById(R.id.giv_spacebg);

        ImageButton playagain = findViewById(R.id.ib_playagain);
        ImageButton enter = findViewById(R.id.ib_enter);

        RecyclerView rvScore = findViewById(R.id.rv_scores);

        EditText enterName = findViewById(R.id.et_name);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enter.setVisibility(View.GONE);
                enterName.setVisibility(View.GONE);
            }
        });


        init();
        //accelerometer = new Accelerometer(this);
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
                switch (player_state){
                    case 0:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hand.setImageResource(R.drawable.hand_still);
                                rvScore.setVisibility(View.GONE);
                                playagain.setVisibility(View.GONE);
                                shp.setVisibility(View.VISIBLE);
                                grid.setVisibility(View.VISIBLE);
                                //giv.setImageResource(R.drawable.spacebg);
                                //giv.setVisibility(View.VISIBLE);
                                spacebg.setImageResource(R.drawable.spacebgtemp);
                                spacebg.setVisibility(View.VISIBLE);

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
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(GameActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, GameActivity.RECORD_AUDIO);
                        } else {
                            startRecorder();
                        }
                        gyroscope.unregister();
                        Log.i ("PLAYER STATE", state);
                        Log.i ("UNREGISTERED", "gyro disabled");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //giv.setVisibility(View.VISIBLE);
                                shp.setVisibility(View.VISIBLE);
                                asteroid.setVisibility(View.VISIBLE);
                            }
                        });

                        runner = new Thread(){
                            public void run()
                            {
                                while (runner != null)
                                {
                                    try
                                    {
                                        Thread.sleep(1000);
                                        Log.i("Noise", EMA_FILTER + "");
                                    } catch (InterruptedException e) { };
                                    mHandler.post(updater);
                                }
                            }
                        };
                        runner.start();
                        Log.d("Noise", "start runner()");

                        switch (asteroid_loc){
                            case 0:
                                if (state.equals("upper_left"))
                                    player_state = 2;
                                else{
                                    score++;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //giv.setVisibility(View.GONE);
                                            spacebg.setVisibility(View.GONE);
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
//                                                    player.reset();
//                                                    player.release();
//                                                    player=null;
                                                    asteroid.setVisibility(View.GONE);
//                                                    asteroid.stopPlayback();
                                                    spacebg.setVisibility(View.VISIBLE);
                                                    //giv.setVisibility(View.VISIBLE);

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
                                    score++;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //giv.setVisibility(View.GONE);
                                            spacebg.setVisibility(View.GONE);
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
//                                                    asteroid.stopPlayback();
                                                    spacebg.setVisibility(View.VISIBLE);
                                                    //giv.setVisibility(View.VISIBLE);
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
                                    score++;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //giv.setVisibility(View.GONE);
                                            spacebg.setVisibility(View.GONE);
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
//                                                    asteroid.stopPlayback();
                                                    spacebg.setVisibility(View.VISIBLE);

                                                    //giv.setVisibility(View.VISIBLE);

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
                                    score++;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //giv.setVisibility(View.GONE);
                                            spacebg.setVisibility(View.GONE);
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
//                                                    asteroid.stopPlayback();
                                                    //giv.setVisibility(View.VISIBLE);
                                                    spacebg.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    });
                                    player_state = 0;
                                }

                                break;
                            default:
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showScore.setText(score+"");

                            }
                        });
                        break;

                    case 2:
                        stopRecorder();
                        Log.i ("PLAYER STATE", "YOU LOSE!");
                        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ast_and_explosion);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                grid.setVisibility(View.GONE);
                                shp.setVisibility(View.GONE);
                                if (hasEnded == 0){
                                    //giv.setVisibility(View.GONE);
                                    spacebg.setVisibility(View.GONE);
                                }

                                //asteroid.setVisibility(View.VISIBLE);
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
                                        //giv.setImageResource(R.drawable.game_over_loop);
                                        //giv.setVisibility(View.VISIBLE);s
                                        spacebg.setImageResource(R.drawable.high_score);
                                        enterName.setVisibility(View.VISIBLE);
                                        enter.setVisibility(View.VISIBLE);
                                        hasEnded = 1;
                                        spacebg.setVisibility(View.VISIBLE);
                                        //playagain.setVisibility(View.VISIBLE);
                                        asteroid.setVisibility(View.GONE);
                                        stopPlayer();
                                        playEnd(view);
                                    }
                                });
                            }
                        });

//                        enter.setOnClickListener(new View.OnClickListener(){
//                            @Override
//                            public void onClick(View v)
//                            {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        playagain.setVisibility(View.VISIBLE);
//                                        spacebg.setImageResource(R.drawable.game_over);
//                                        rvScore.setVisibility(View.VISIBLE);
//                                        enterName.setVisibility(View.GONE);
//                                        enter.setVisibility(View.GONE);
//                                    }
//                                });
//                            }
//                        });

                        playagain.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                //spacebg.setImageResource(R.drawable.spacebgtemp);

                                Intent i = new Intent (GameActivity.this, MainActivity.class);
                                startActivity (i);
                                stopPlayer();
                            }
                        });
                        break;
                    default:
                }
            }
        };

        t.schedule(tt,0,6000);

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
                    if (Math.abs(rY) > 4.0f){ //rotate right
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hand.setImageResource(R.drawable.hand_right);
                                    }
                                });

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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hand.setImageResource(R.drawable.hand_right);
                                    }
                                });
                            }
                        }
                    }
                    else if (Math.abs(rY) < 4.0f){ //rotate left

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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hand.setImageResource(R.drawable.hand_left);
                                    }
                                });
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hand.setImageResource(R.drawable.hand_left);
                                    }
                                });
                            }
                        }
                    }
                }
                else{
                    if (Math.abs(rX) > 4.0f){ //rotate downward
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hand.setImageResource(R.drawable.hand_down);
                                    }
                                });
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hand.setImageResource(R.drawable.hand_down);
                                    }
                                });
                            }
                        }
                    }
                    else if (Math.abs(rX) < 4.0f){ //rotate upward
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hand.setImageResource(R.drawable.hand_up);
                                    }
                                });
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hand.setImageResource(R.drawable.hand_up);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gyroscope.register();

        play(binding.getRoot());
    }

    @Override
    protected void onPause() {
        super.onPause();
        gyroscope.unregister();
        stopRecorder();
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

    private void init() {
        ScoreDAO scoreDAO = new ScoreDAOFirebaseImpl(getApplicationContext());
        scoreAdapter = new ScoreAdapter(getApplicationContext(),
                scoreDAO.getAllScores());

        RecyclerView rvScore = findViewById(R.id.rv_scores);

        rvScore.setLayoutManager (new LinearLayoutManager(getApplicationContext()));
        rvScore.setAdapter(scoreAdapter);
//        binding.rvScores.setLayoutManager (new LinearLayoutManager(getApplicationContext()));
//        binding.rvScores.setAdapter(scoreAdapter);

        binding.ibEnter.setOnClickListener (view -> {
            TextView scoreText = findViewById(R.id.tv_showScore);
            //scoreText.setText(binding.etName.getText().toString() + "has score: " + score);
            Score playerScore = new Score();
            playerScore.setScore(score);
            playerScore.setName (binding.etName.getText().toString());
            if (playerScore.getName().isEmpty())
                playerScore.setName("AAA");
            scoreDAO.addScore(playerScore);
            scoreAdapter.addScores(scoreDAO.getAllScores());

//            for (int i = 0; i<scoreDAO.getTop10Scores().size(); i++)
//                Log.i ("SCORE FOUND", scoreDAO.getTop10Scores().toString());

            ImageButton playagain = findViewById(R.id.ib_playagain);
            ImageView spacebg = findViewById(R.id.iv_spacebg);

            EditText enterName = findViewById(R.id.et_name);
            ImageButton enter = findViewById(R.id.ib_enter);

            scoreAdapter.addScores(scoreDAO.getAllScores());
            scoreAdapter.notifyDataSetChanged();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            spacebg.setImageResource(R.drawable.game_over);
                            rvScore.setVisibility(View.VISIBLE);
                            enterName.setText("");
                            enterName.setVisibility(View.GONE);
                            enter.setVisibility(View.GONE);
                            playagain.setVisibility(View.VISIBLE);
                        }
                    });
                    score = 0;
                }
            }, 2000);
        });
    }

    public void startRecorder() {
        if (mRecorder == null)  {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");

            try  {
                mRecorder.prepare();
            } catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " + android.util.Log.getStackTraceString(ioe));
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }

            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
        }
    }

    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv(){
        mStatusView.setText(Double.toString((getAmplitudeEMA())) + " dB");

    }
    public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitudeEMA() / ampl);
    }
    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
}
