package com.dungeoncrawl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Win extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        final Intent intent = new Intent(this, MainActivity.class);

        SharedPreferences scoreBoard = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int score = scoreBoard.getInt("key", 0); //0 is the default value
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText("High score: " + score);

        ViewGroup layout = (ViewGroup) findViewById(R.id.highScore);
        layout.addView(textView);

        final Button playAgain = (Button) findViewById(R.id.playAgain);
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(intent);
                finish();
            }
        });

        final Button quit = (Button) findViewById(R.id.quit);
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(intent);
                finish();
            }
        });
    }
}
