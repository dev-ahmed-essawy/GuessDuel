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

    LinearLayout keypadContainer;
    
    String name1, name2;

    final int WIN_SCORE = 5;

    MediaPlayer clickSound, winSound, loseSound;
    CountDownTimer timer;

    boolean isActive = true;
    boolean isAI = false;

    // ✅ Smart AI range
    int aiMin = 0;
    int aiMax = 100;

    // ✅ Keypad
    TextView display;
    StringBuilder inputVal = new StringBuilder();

    TextView result, scoreText, turnText, livesText, timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        keypadContainer = findViewById(R.id.keypadContainer)
        display = findViewById(R.id.inputDisplay);
        result = findViewById(R.id.resultText);
        scoreText = findViewById(R.id.scoreText);
        turnText = findViewById(R.id.turnText);
        livesText = findViewById(R.id.livesText);
        timerText = findViewById(R.id.timerText);

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

        guesser = (setter == 1) ? 2 : 1;

        setupKeypad();

        startRound();
    }

    // ✅ KEYPAD LOGIC
    private void setupKeypad() {

        int[] btns = {
                R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,
                R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9
        };

        for (int id : btns) {
            Button b = findViewById(id);
            b.setOnClickListener(v -> {
            
                // ✅ animation
                v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(50)
                    .withEndAction(() ->
                        v.animate().scaleX(1f).scaleY(1f).start()
                    );
            
                // ✅ your original logic
                if (inputVal.length() < 3) {
                    inputVal.append(b.getText());
                    display.setText(inputVal.toString());
                }
            });

        }

        // DELETE
        findViewById(R.id.btnDel).setOnClickListener(v -> {
            
                // ✅ animation
                v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(50)
                    .withEndAction(() ->
                        v.animate().scaleX(1f).scaleY(1f).start()
                    );
            if (inputVal.length() > 0) {
                inputVal.deleteCharAt(inputVal.length() - 1);
                display.setText(inputVal.length()==0 ? "0" : inputVal.toString());
            }
        });

        // OK (GUESS)
        findViewById(R.id.btnOk).setOnClickListener(v -> {
            
                // ✅ animation
                v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(50)
                    .withEndAction(() ->
                        v.animate().scaleX(1f).scaleY(1f).start()
                    );

            if (inputVal.length() == 0) return;

            int g = Integer.parseInt(inputVal.toString());

            inputVal.setLength(0);
            display.setText("0");

            handleGuess(g);
        });
    }

    private void startRound() {

        lives = 5;

        inputVal.setLength(0);
        display.setText("0");
        result.setText("");

        timerText.setTextColor(0xFFFFFFFF);

        turnText.setText(getName(guesser) + " guessing...");
        updateUI();

        // ✅ AI turn
        if (isAI && guesser == 2) {

            keypadContainer.setVisibility(View.GONE);  // ❌ hide keypad
            runAI();
        
        } else {
        
            keypadContainer.setVisibility(View.VISIBLE); // ✅ show keypad
            startTimer();
        }
    }

    private void startTimer() {

        if (!isActive) return;

        cancelTimer();

        timer = new CountDownTimer(timerSeconds * 1000, 1000) {

            public void onTick(long ms) {
                int s = (int) (ms / 1000);
                timerText.setText("⏳ " + s + "s");

                if (s <= 5)
                    timerText.setTextColor(0xFFFF5252);
                else
                    timerText.setTextColor(0xFFFFFFFF);
            }

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

    private void handleGuess(int g) {

        if (clickSound != null) clickSound.start();

        cancelTimer();

        if (g < secret) {
            result.setText("⬆ Higher");
            lives--;
        }
        else if (g > secret) {
            result.setText("⬇ Lower");
            lives--;
        }
        else {

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

    // ✅ SMART AI
    private void runAI() {

        result.postDelayed(() -> {

            int guess = (aiMin + aiMax) / 2;

            if (guess < secret) {
                result.setText("AI: " + guess + " ⬆");
                aiMin = guess + 1;
                lives--;
            }
            else if (guess > secret) {
                result.setText("AI: " + guess + " ⬇");
                aiMax = guess - 1;
                lives--;
            }
            else {

                if (winSound != null) winSound.start();

                result.setText("AI guessed " + guess + " ✅");

                score(2);

                aiMin = 0;
                aiMax = 100;

                if (checkWinner()) return;

                nextTurn();
                return;
            }

            updateUI();

            if (lives <= 0) {

                if (loseSound != null) loseSound.start();

                score(1);

                aiMin = 0;
                aiMax = 100;

                if (checkWinner()) return;

                nextTurn();
                return;
            }

            runAI();

        }, 1000);
    }

    private void score(int p) {
        if (p == 1) scoreP1++;
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
