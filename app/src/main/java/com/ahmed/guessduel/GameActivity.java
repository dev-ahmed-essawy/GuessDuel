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

    EditText input;
    TextView result, livesText, scoreText;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        input = findViewById(R.id.input);
        result = findViewById(R.id.resultText);
        livesText = findViewById(R.id.livesText);
        scoreText = findViewById(R.id.scoreText);
        btn = findViewById(R.id.submitBtn);

        clickSound = MediaPlayer.create(this, R.raw.click);

        // ✅ Receive secret from Player 1
        if (getIntent().hasExtra("secret")) {
            secret = getIntent().getIntExtra("secret", 0);
        } else {
            secret = (int)(Math.random() * 100) + 1;
        }

        updateUI();

        btn.setOnClickListener(v -> {

            v.animate()
                    .scaleX(0.9f).scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100)
                    );

            if (clickSound != null) {
                try { clickSound.start(); } catch (Exception ignored) {}
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
                    result.setText("✅ Player 2 Wins!");
                }

                updateUI();

                if (lives <= 0) {
                    result.setText("💀 Player 1 Wins! Score: " + score);

                    result.postDelayed(() -> {
                        restartGame();
                    }, 2000);
                }

            } catch (Exception e) {
                result.setText("Invalid input!");
            }
        });
    }

    private void updateUI() {
        livesText.setText("Lives: " + lives + " ❤️");
        scoreText.setText("Score: " + score);
    }

    private void restartGame() {
        score = 0;
        lives = 5;
        secret = (int)(Math.random() * 100) + 1;

        input.setText("");
        result.setText("New round!");
        btn.setEnabled(true);

        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (clickSound != null) {
            clickSound.release();
            clickSound = null;
        }
    }
}
