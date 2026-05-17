package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText p1 = findViewById(R.id.player1Input);
        EditText p2 = findViewById(R.id.player2Input);
        Button start = findViewById(R.id.startBtn);

        start.setOnClickListener(v -> {

            String name1 = p1.getText().toString();
            String name2 = p2.getText().toString();

            if (name1.isEmpty()) name1 = "Player 1";
            if (name2.isEmpty()) name2 = "AI"; // ✅ AI support

            Intent i = new Intent(this, DifficultyActivity.class);
            i.putExtra("name1", name1);
            i.putExtra("name2", name2);

            startActivity(i);
        });
    }
}
