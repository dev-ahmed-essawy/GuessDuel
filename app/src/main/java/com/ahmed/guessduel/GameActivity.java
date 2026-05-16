package com.ahmed.guessduel;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.*;

public class GameActivity extends Activity {

    int secret;
    int lives = 5;

    int currentPlayer = 1; // 1 or 2
    boolean isSettingPhase = true;

    MediaPlayer clickSound;

    EditText input;
    TextView result, livesText, turnText;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        input = findViewById(R.id.input);
        result = findViewById(R.id.resultText);
        livesText = findViewById(R.id.livesText);
        turnText = findViewById(R.id.turnText);
        btn = findViewById(R.id.submitBtn);

        clickSound = MediaPlayer.create(this, R.raw.click);

        startSettingPhase();
    }

    // ✅ PHASE 1: Player sets secret
    private void startSettingPhase() {
        isSettingPhase = true;

        input.setText("");
        result.setText("");
        lives = 5;

        turnText.setText("Player " + currentPlayer + " set a secret number");
    }

    // ✅ PHASE 2: Other player guesses
    private void startGuessingPhase() {
        isSettingPhase = false;

        input.setText("");
        lives = 5;

        int guesser = (currentPlayer == 1) ? 2 : 1;
        turnText.setText("Player " + guesser + " guessing...");
        updateLives();
    }

    private void updateLives() {
        livesText.setText("Lives: " + lives + " ❤️");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (clickSound != null) {
            clickSound.release();
            clickSound = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        btn.setOnClickListener(v -> {

            // Animation
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100));

            // Sound
            if (clickSound != null) {
                try { clickSound.start(); } catch (Exception ignored) {}
            }

            String text = input.getText().toString();
            if (text.isEmpty()) return;

            try {
                int value = Integer.parseInt(text);

                // ✅ SETTING PHASE
                if (isSettingPhase) {

                    secret = value;

                    result.setText("✅ Secret set!");
                    startGuessingPhase();

                }
                // ✅ GUESSING PHASE
                else {

                    if (value < secret) {
                        result.setText("⬆ Higher");
                        lives--;
                    }
                    else if (value > secret) {
                        result.setText("⬇ Lower");
                        lives--;
                    }
                    else {
                        int winner = (currentPlayer == 1) ? 2 : 1;
                        result.setText("✅ Player " + winner + " guessed correctly!");

                        switchTurn();
                        return;
                    }

                    updateLives();

                    if (lives <= 0) {
                        result.setText("💀 Player " + currentPlayer + " wins!");

                        switchTurn();
                    }
                }

            } catch (Exception e) {
                result.setText("Invalid input!");
            }
        });
    }

    // ✅ SWITCH TURN
    private void switchTurn() {

        // Switch player
        currentPlayer = (currentPlayer == 1) ? 2 : 1;

        // Delay then new round
        result.postDelayed(() -> {
            startSettingPhase();
        }, 2000);
    }
}
