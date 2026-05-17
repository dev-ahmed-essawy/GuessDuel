package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.*;

public class GameActivity extends Activity {

    int secret, lives = 5;
    int setter, guesser;
    int scoreP1, scoreP2;

    String name1, name2;

    final int WIN_SCORE = 5;

    EditText input;
    TextView result, livesText, turnText, scoreText;
    Button btn;

    MediaPlayer clickSound, winSound, loseSound;

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
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);

        secret = getIntent().getIntExtra("secret", 0);
        setter = getIntent().getIntExtra("setter", 1);
        scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        scoreP2 = getIntent().getIntExtra("scoreP2", 0);
        name1 = getIntent().getStringExtra("name1");
        name2 = getIntent().getStringExtra("name2");

        guesser = (setter == 1) ? 2 : 1;

        startRound();

        btn.setOnClickListener(v -> handleGuess());
    }

    private String getName(int p) {
        return (p == 1) ? name1 : name2;
    }

    private void startRound() {
        lives = 5;
        input.setText("");
        result.setText("");
        turnText.setText(getName(guesser) + " guessing...");
        updateUI();
    }

    private void updateUI() {
        livesText.setText("Lives: " + lives);
        scoreText.setText(name1 + ": " + scoreP1 + " | " + name2 + ": " + scoreP2);
    }

    private void handleGuess() {

        if (clickSound != null) clickSound.start();

        String text = input.getText().toString();
        if (text.isEmpty()) return;

        int g = Integer.parseInt(text);

        if (g < secret) {
            result.setText("Higher");
            lives--;
        } else if (g > secret) {
            result.setText("Lower");
            lives--;
        } else {

            if (winSound != null) winSound.start();

            if (guesser == 1) scoreP1++;
            else scoreP2++;

            result.setText(getName(guesser) + " wins round!");
            result.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));

            if (checkWinner()) return;

            nextTurn();
            return;
        }

        updateUI();

        if (lives <= 0) {

            if (loseSound != null) loseSound.start();

            if (setter == 1) scoreP1++;
            else scoreP2++;

            result.setText(getName(setter) + " wins round!");

            if (checkWinner()) return;

            nextTurn();
        }
    }

    private boolean checkWinner() {

        if (scoreP1 >= WIN_SCORE) {
            openResult(name1);
            return true;
        }

        if (scoreP2 >= WIN_SCORE) {
            openResult(name2);
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
            i.putExtra("name1", name1);
            i.putExtra("name2", name2);
            startActivity(i);
            finish();
        }, 1000);
    }

    private void openResult(String winner) {

        Intent i = new Intent(this, ResultActivity.class);
        i.putExtra("winner", winner);
        startActivity(i);
        finish();
    }
}
