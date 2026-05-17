package com.ahmed.guessduel;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultText = findViewById(R.id.resultText);
        Button restartBtn = findViewById(R.id.restartBtn);
        ImageView trophy = findViewById(R.id.trophy);

        // ✅ Get winner name
        String winner = getIntent().getStringExtra("winner");

        if (winner == null) winner = "Player";

        // ✅ Show winner
        resultText.setText("🏆 " + winner + " WINS!");

        // 🎨 Trophy animation
        trophy.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(500)
                .withEndAction(() ->
                        trophy.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(500)
                );

        // 🔁 Restart game properly
        restartBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);

            // ✅ Clear back stack (no bug returns)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(i);
        });
    }
}

