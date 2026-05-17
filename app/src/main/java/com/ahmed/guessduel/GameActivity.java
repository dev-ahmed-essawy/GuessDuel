package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.*;

public class GameActivity extends Activity {

    int secret, lives;
    int setter, guesser;
    int scoreP1, scoreP2;
    int timerSeconds;

    String name1, name2;

    final int WIN_SCORE = 5;

    MediaPlayer clickSound, winSound, loseSound;
    CountDownTimer timer;

    boolean isActive = true;
    boolean isAI = false;

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

        clickSound = MediaPlayer.create(this, R.raw.click);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);

        // ✅ Data
        secret = getIntent().getIntExtra("secret", 0);
        setter = getIntent().getIntExtra("setter", 1);
        timerSeconds = getIntent().getIntExtra("timer", 15);

        scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        scoreP2 = getIntent().getIntExtra("scoreP2", 0);

        name1 = getIntent().getStringExtra("name1");
        name2 = getIntent().getStringExtra("name2");

        if (name1 == null) name1 = "Player 1";
        if (name2 == null) name2 = "Player 2";

        isAI = name2.equals("AI");

        if (isAI) {
            setter = 1;     // human sets
            guesser = 2;    // AI guesses ✅
        } else {
            guesser = (setter == 1) ? 2 : 1;
        }

        startRound();

        btn.setOnClickListener(v -> handleGuess());
    }

    private void startRound() {
        lives = 5;

        input.setText("");
        result.setText("");

        input.setEnabled(true);
        btn.setEnabled(true);

        timerText.setTextColor(0xFFFFFFFF);

        turnText.setText(getName(guesser) + " guessing...");
        updateUI();

        if (isAI && guesser == 2) {
            runAI();
        } else {
            startTimer();
        }
    }

    private void startTimer() {

        if (!isActive) return;

        cancelTimer();

        timer = new CountDownTimer(timerSeconds * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                if (!isActive) return;

                int seconds = (int) (millisUntilFinished / 1000);

                timerText.setText("⏳ " + seconds + "s");

                if (seconds <= 5)
                    timerText.setTextColor(0xFFFF5252);
                else
                    timerText.setTextColor(0xFFFFFFFF);
            }

            @Override
            public void onFinish() {

                if (!isActive) return;

                result.setText("⏰ Time's up!");
                lives--;

                updateUI();

                if (lives <= 0) {
                    score(setter);
                    if (checkWinner()) return;
                    nextTurn();
                    return;
                }

                startTimer();
            }
        }.start();
    }

    private void handleGuess() {

        if (clickSound != null) clickSound.start();

        cancelTimer();

        String text = input.getText().toString().trim();
        if (text.isEmpty()) return;

        int g;
        try {
            g = Integer.parseInt(text);
        } catch (Exception e) {
            input.setError("Invalid number");
            startTimer();
            return;
        }

        if (g < secret) {
            result.setText("⬆ Higher");
            lives--;
        } else if (g > secret) {
            result.setText("⬇ Lower");
            lives--;
        } else {

            if (winSound != null) winSound.start();

            score(guesser);

            if (checkWinner()) return;

            nextTurn();
            return;
        }

        updateUI();

        if (lives <= 0) {

            if (loseSound != null) loseSound.start();

            score(setter);

            if (checkWinner()) return;

            nextTurn();
            return;
        }

        startTimer();
    }

    private void runAI() {

        input.setEnabled(false);
        btn.setEnabled(false);

        result.postDelayed(() -> {

            int guess = (int) (Math.random() * 101);

            if (guess < secret) {
                result.setText("AI: " + guess + " ⬆");
                lives--;
            } else if (guess > secret) {
                result.setText("AI: " + guess + " ⬇");
                lives--;
            } else {

                if (winSound != null) winSound.start();

                score(2);

                if (checkWinner()) return;

                nextTurn();
                return;
            }

            updateUI();

            if (lives <= 0) {

                if (loseSound != null) loseSound.start();

                score(1);

                if (checkWinner()) return;

                nextTurn();
                return;
            }

            runAI();

        }, 1200);
    }

    private void score(int player) {
        if (player == 1) scoreP1++;
        else scoreP2++;
    }

    private void updateUI() {
        scoreText.setText(name1 + ": " + scoreP1 + " | " + name2 + ": " + scoreP2);
        livesText.setText("Lives: " + lives);
    }

    private String getName(int p) {
        return (p == 1) ? name1 : name2;
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

    private void openResult(String winner) {

        isActive = false;
        cancelTimer();

        Intent i = new Intent(GameActivity.this, ResultActivity.class);
        i.putExtra("winner", winner);
        
        // ✅ CLEAR STACK
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(i);
        finish();
    }

    private void nextTurn() {

        isActive = false;
        cancelTimer();

        int newSetter = guesser;

        Intent i = new Intent(GameActivity.this, SetupActivity.class);
        i.putExtra("setter", newSetter);
        i.putExtra("scoreP1", scoreP1);
        i.putExtra("scoreP2", scoreP2);
        i.putExtra("name1", name1);
        i.putExtra("name2", name2);
        i.putExtra("timer", timerSeconds);
        
        // ✅ CLEAR STACK
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(i);
        finish();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isActive = false;
        cancelTimer();

        if (clickSound != null) clickSound.release();
        if (winSound != null) winSound.release();
        if (loseSound != null) loseSound.release();
    }
}
