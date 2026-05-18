package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class DifficultyActivity extends Activity {

    Button easyBtn;
    Button mediumBtn;
    Button hardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        // ✅ FIND VIEWS
        easyBtn = findViewById(R.id.easyBtn);
        mediumBtn = findViewById(R.id.mediumBtn);
        hardBtn = findViewById(R.id.hardBtn);

        // ✅ EASY MODE
        easyBtn.setOnClickListener(v -> {
            startGame(20);
        });

        // ✅ MEDIUM MODE
        mediumBtn.setOnClickListener(v -> {
            startGame(15);
        });

        // ✅ HARD MODE
        hardBtn.setOnClickListener(v -> {
            startGame(10);
        });
    }

    // ✅ START AI GAME
    private void startGame(int timerSeconds) {

        Intent intent = new Intent(
                DifficultyActivity.this,
                AIGameActivity.class
        );

        // ✅ SEND TIMER VALUE
        intent.putExtra(
                "timer",
                timerSeconds
        );

        startActivity(intent);
    }
}
