package com.tutsplus.mylittlegame;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyLittleGame";
    private Button mainButton;
    private TextView scoreView;
    private TextView timeView;

    private int score;
    private boolean playing = false;

    private GoogleApiClient apiClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.e(TAG, "Could not connect to Play games services");
                finish();
            }
        }).build();

        mainButton = (Button)findViewById(R.id.main_button);
        scoreView = (TextView)findViewById(R.id.score_view);
        timeView = (TextView)findViewById(R.id.time_view);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!playing) {
                    playing = true;
                    score = 0;
                    mainButton.setText("Keep Clicking");
                    new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            timeView.setText("Time remaining: " + millisUntilFinished/1000);
                        }

                        @Override
                        public void onFinish() {
                            playing = false;
                            timeView.setText("Game over");
                            mainButton.setVisibility(View.GONE);

                            Games.Leaderboards.submitScore(apiClient,
                                    getString(R.string.leaderboard_my_little_leaderboard),
                                    score);
                        }
                    }.start();
                } else {
                    score++;
                    scoreView.setText("Score: " + score + " points");

                    if(score>100) {
                        Games.Achievements.unlock(apiClient, getString(R.string.achievement_lightning_fast));
                    }
                }
            }
        });
    }


    public void showLeaderboard(View v) {
        startActivityForResult(
                Games.Leaderboards.getLeaderboardIntent(apiClient,
                        getString(R.string.leaderboard_my_little_leaderboard)), 0);
    }

    public void showAchievements(View v) {
        startActivityForResult(Games.Achievements.getAchievementsIntent(apiClient), 1);
    }
}
