package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.*;

public class GameActivity extends Activity {

    int secret;
    int lives = 5;

    int setter;
    int guesser;

    int scoreP1 = 0;
    int scoreP2 = 0;

    final int WIN_SCORE = 5; // 🏆 target

    MediaPlayer clickSound;

    EditText input;
    TextView result, livesText, turnText, scoreText;
    Button btn;

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
    }

    private void startRound() {
        lives = 5;
        input.setText("");
        result.setText("");

        turnText.setText("Player " + guesser + " guessing...");
        btn.setText("Guess ✅");

        updateUI();
    }

    private void updateUI() {
        livesText.setText("Lives: " + lives + " ❤️");
        scoreText.setText("P1: " + scoreP1 + "  |  P2: " + scoreP2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clickSound != null) clickSound.release();
    }

    @Override
    protected void onResume() {
        super.onResume();

        btn.setOnClickListener(v -> {

            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100));

            if (clickSound != null) {
                try { clickSound.start(); } catch (Exception ignored) {}
            }

            String text = input.getText().toString();
            if (text.isEmpty()) return;

            try {
                int value = Integer.parseInt(text);

                if (value < secret) {
                    result.setText("⬆ Higher");
                    lives--;
                }
                else if (value > secret) {
                    result.setText("⬇ Lower");
                    lives--;
                }
                else {
                    // ✅ Guesser wins round
                    if (guesser == 1) scoreP1++;
                    else scoreP2++;

                    result.setText("✅ Player " + guesser + " wins round!");

                    if (checkGameWinner()) return;

                    switchTurn();
                    return;
                }

                updateUI();

                if (lives <= 0) {
                    // ✅ Setter wins round
                    if (setter == 1) scoreP1++;
                    else scoreP2++;

                    result.setText("💀 Player " + setter + " wins round!");

                    if (checkGameWinner()) return;

                    switchTurn();
                }

            } catch (Exception e) {
                result.setText("Invalid input!");
            }
        });
    }

    // ✅ CHECK FINAL WINNER
    private boolean checkGameWinner() {

        if (scoreP1 >= WIN_SCORE) {
            result.setText("🏆 Player 1 WINS THE GAME!");
            btn.setEnabled(false);
            return true;
        }

        if (scoreP2 >= WIN_SCORE) {
            result.setText("🏆 Player 2 WINS THE GAME!");
            btn.setEnabled(false);
            return true;
        }

        return false;
    }

    private void switchTurn() {

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
}
