package com.ahmed.guessduel;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.*;

public class GameActivity extends Activity {

    int secret;
    int score = 0;
    int lives = 5;

    MediaPlayer clickSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // UI Elements
        EditText input = findViewById(R.id.input);
        TextView result = findViewById(R.id.resultText);
        TextView livesText = findViewById(R.id.livesText);
        TextView scoreText = findViewById(R.id.scoreText);
        Button btn = findViewById(R.id.submitBtn);

        // Setup sound
        clickSound = MediaPlayer.create(this, R.raw.click);

        // Random number
        secret = (int)(Math.random() * 100) + 1;

        // Initial UI
        livesText.setText("Lives: " + lives + " ❤️");
        scoreText.setText("Score: " + score);

        // Button click
        btn.setOnClickListener(v -> {

            // 💥 Button Animation
            v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100)
                    );

            // 🔊 Play Sound (SAFE)
            if (clickSound != null) {
                try {
                    clickSound.start();
                } catch (Exception ignored) {}
            }

            String text = input.getText().toString();

            if (text.isEmpty()) {
                result.setText("Enter a number!");
                return;
            }

            try {
                int g = Integer.parseInt(text);

                if (g < secret) {
                    result.setText("⬆ Higher");
                    lives--;
                } else if (g > secret) {
                    result.setText("⬇ Lower");
                    lives--;
                } else {
                    score += 10;
                    result.setText("✅ Correct!");

                    // New round
                    secret = (int)(Math.random() * 100) + 1;
                }

                // Update UI
                livesText.setText("Lives: " + lives + " ❤️");
                scoreText.setText("Score: " + score);

                // Game over
                if (lives <= 0) {
                    result.setText("💀 Game Over! Final Score: " + score);
                    btn.setEnabled(false);
                }

            } catch (Exception e) {
                result.setText("Invalid input!");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // ✅ Release sound properly
        if (clickSound != null) {
            clickSound.release();
            clickSound = null;
        }
    }
}
