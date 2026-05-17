package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class DifficultyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        Button easy = findViewById(R.id.easyBtn);
        Button medium = findViewById(R.id.mediumBtn);
        Button hard = findViewById(R.id.hardBtn);

        String name1 = getIntent().getStringExtra("name1");
        String name2 = getIntent().getStringExtra("name2");

        easy.setOnClickListener(v -> startGame(20, name1, name2));
        medium.setOnClickListener(v -> startGame(15, name1, name2));
        hard.setOnClickListener(v -> startGame(10, name1, name2));
    }

    private void startGame(int timer, String n1, String n2) {

        Intent i = new Intent(this, SetupActivity.class);
        i.putExtra("timer", timer);
        i.putExtra("setter", 1);
        i.putExtra("scoreP1", 0);
        i.putExtra("scoreP2", 0);
        i.putExtra("name1", n1);
        i.putExtra("name2", n2);

        startActivity(i);
    }
}
