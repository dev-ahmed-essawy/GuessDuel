package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultText = findViewById(R.id.resultText);
        Button restartBtn = findViewById(R.id.restartBtn);

        String winner = getIntent().getStringExtra("winner");
        if (winner == null) {
            winner = "Player";
        }

        resultText.setText("🏆 " + winner + " WINS!");

        // ✅ FIXED HERE
        restartBtn.setOnClickListener(v -> {
            Intent i = new Intent(ResultActivity.this, ModeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}
