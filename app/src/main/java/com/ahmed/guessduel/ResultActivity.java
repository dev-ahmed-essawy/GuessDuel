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

        // ✅ Get winner safely
        String winner = getIntent().getStringExtra("winner");
        if (winner == null) {
            winner = "Player";
        }

        // ✅ Show result
        resultText.setText("🏆 " + winner + " WINS!");

        // ✅ Restart game → back to MainActivity
        restartBtn.setOnClickListener(v -> {
            Intent i = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}
