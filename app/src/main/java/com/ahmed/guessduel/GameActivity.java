package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.*;

public class GameActivity extends Activity {

    int secret, lives = 5;
    int setter, guesser;
    int scoreP1, scoreP2;

    String name1, name2;

    final int WIN_SCORE = 5;

    MediaPlayer clickSound, winSound, loseSound;

    CountDownTimer timer;

    EditText input;
    TextView result, scoreText, turnText, livesText, timerText;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        input = findViewById(R.id.input);
        result = findViewById(R.id.resultText);
        scoreText = findViewById(R.id.scoreText);
        turnText = findViewById(R.id.turnText);
        livesText = findViewById(R.id.livesText);
        timerText = findViewById(R.id.timerText);
        btn = findViewById(R.id.submitBtn);

        // ✅ Sounds
        clickSound = MediaPlayer.create(this, R.raw.click);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);

        // ✅ Data
        secret = getIntent().getIntExtra("secret", 0);
        setter = getIntent().getIntExtra("setter", 1);
        scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        scoreP2 = getIntent().getIntExtra("scoreP2", 0);
        name1 = getIntent().getStringExtra("name1");
        name2 = getIntent().getStringExtra("name2");

        if (name1 == null) name1 = "Player 1";
        if (name2 == null) name2 = "Player 2";

        guesser = (setter == 1) ? 2 : 1;

        startRound();

        btn.setOnClickListener(v -> handleGuess());
    }

    private void startRound() {
        lives = 5;
        input.setText("");
        result.setText("");

        turnText.setText(getName(guesser) + " guessing...");
        updateUI();

        startTimer(); // ✅ NEW FEATURE
    }

    private void startTimer() {

        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {

                result.setText("⏰ Time's up!");
                lives--;

                updateUI();

                if (lives <= 0) {

                    if (loseSound != null) loseSound.start();

                    if (setter == 1) scoreP1++;
                    else scoreP2++;

                    if (checkWinner()) return;

                    nextTurn();
                    return;
                }

                startTimer(); // restart
            }
        }.start();
    }

    private String getName(int p) {
        return (p == 1) ? name1 : name2;
    }

    private void updateUI() {
        scoreText.setText(name1 + ": " + scoreP1 + " | " + name2 + ": " + scoreP2);
        livesText.setText("Lives: " + lives);
    }

    private void handleGuess() {

        if (clickSound != null) clickSound.start();

        if (timer != null) timer.cancel(); // ✅ STOP TIMER

        String text = input.getText().toString();
        if (text.isEmpty()) return;

        int g = Integer.parseInt(text);

        if (g < secret) {
            result.setText("⬆ Higher");
            lives--;
        } else if (g > secret) {
            result.setText("⬇ Lower");
            lives--;
        } else {

            if (winSound != null) winSound.start();

            if (guesser == 1) scoreP1++;
            else scoreP2++;

            if (checkWinner()) return;

            nextTurn();
            return;
        }

        updateUI();

        if (lives <= 0) {

            if (loseSound != null) loseSound.start();

            if (setter == 1) scoreP1++;
            else scoreP2++;

            if (checkWinner()) return;

            nextTurn();
            return;
        }

        startTimer(); // ✅ RESTART TIMER
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
            Intent i = new Intent(GameActivity.this, SetupActivity.class);
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

        if (timer != null) timer.cancel(); // ✅ STOP TIMER

        Intent i = new Intent(GameActivity.this, ResultActivity.class);
        i.putExtra("winner", winner);

        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (clickSound != null) clickSound.release();
        if (winSound != null) winSound.release();
        if (loseSound != null) loseSound.release();

        if (timer != null) timer.cancel();
    }
}
