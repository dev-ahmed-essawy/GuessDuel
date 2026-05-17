package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class PlayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        boolean isAI = getIntent().getBooleanExtra("isAI", false);

        EditText p1 = findViewById(R.id.player1Input);
        EditText p2 = findViewById(R.id.player2Input);
        Button start = findViewById(R.id.startBtn);

        if (isAI) {
            p2.setText("AI");
            p2.setEnabled(false);
        }

        start.setOnClickListener(v -> {

            String name1 = p1.getText().toString();
            String name2 = isAI ? "AI" : p2.getText().toString();

            if (name1.isEmpty()) name1 = "Player 1";
            if (!isAI && name2.isEmpty()) name2 = "Player 2";

            Intent i = new Intent(this, DifficultyActivity.class);
            i.putExtra("name1", name1);
            i.putExtra("name2", name2);

            startActivity(i);
        });
    }
}
