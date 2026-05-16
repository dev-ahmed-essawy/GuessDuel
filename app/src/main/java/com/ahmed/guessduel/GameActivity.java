package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.*;

public class GameActivity extends Activity {

    int secret, lives = 5;
    int setter, guesser;
    int scoreP1, scoreP2;

    final int WIN_SCORE = 5;

    EditText input;
    TextView result, livesText, turnText, scoreText;
    Button btn;

    MediaPlayer clickSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        input = findViewById(R.id.input);
        result = findViewById(R.id.resultText);
        livesText = findViewById(R.id.livesText);
        turnText = findViewById(R.id.turnText);
        scoreText = findViewById(R.id.scoreText);
        btn = findViewById(R.id.submitBtn);

        clickSound = MediaPlayer.create(this, R.raw.click);

        secret = getIntent().getIntExtra("secret", 0);
        setter = getIntent().getIntExtra("setter", 1);
        scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        scoreP2 = getIntent().getIntExtra("scoreP2", 0);

        guesser = (setter == 1) ? 2 : 1;

        startRound();

        btn.setOnClickListener(v -> handleGuess());
    }

    private void startRound() {
        lives = 5;
        input.setText("");
        result.setText("");

        turnText.setText("Player " + guesser + " guessing...");
        updateUI();
    }

    private void updateUI() {
        livesText.setText("Lives: " + lives + " ❤️");
        scoreText.setText("P1: " + scoreP1 + " | P2: " + scoreP2);
    }

    private void handleGuess() {

        String text = input.getText().toString();
        if (text.isEmpty()) return;

        if (clickSound != null) {
            try { clickSound.start(); } catch (Exception ignored) {}
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

                if (guesser == 1) scoreP1++;
                else scoreP2++;

                result.setText("✅ Player " + guesser + " wins round!");

                if (checkWinner()) return;

                nextTurn();
                return;
            }

            updateUI();

            if (lives <= 0) {
                if (setter == 1) scoreP1++;
                else scoreP2++;

                result.setText("💀 Player " + setter + " wins round!");

                if (checkWinner()) return;

                nextTurn();
            }

        } catch (Exception e) {
            result.setText("Invalid input!");
        }
    }

    private boolean checkWinner() {

        if (scoreP1 >= WIN_SCORE) {
            result.setText("🏆 Player 1 WINS GAME!");
            btn.setEnabled(false);
            return true;
        }

        if (scoreP2 >= WIN_SCORE) {
            result.setText("🏆 Player 2 WINS GAME!");
            btn.setEnabled(false);
            return true;
        }

        return false;
    }

    private void nextTurn() {

        int newSetter = guesser;

        result.postDelayed(() -> {
            Intent i = new Intent(this, SetupActivity.class);
            i.putExtra("setter", newSetter);
            i.putExtra("scoreP1", scoreP1);
            i.putExtra("scoreP2", scoreP2);
            startActivity(i);
            finish();
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clickSound != null) clickSound.release();
    }
}
