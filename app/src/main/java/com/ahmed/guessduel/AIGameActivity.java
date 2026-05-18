package com.ahmed.guessduel;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class AIGameActivity extends Activity {

    // ✅ UI
    TextView turnText;
    TextView resultText;
    TextView timerText;
    TextView inputDisplay;

    LinearLayout keypadContainer;

    // ✅ INPUT
    StringBuilder inputVal = new StringBuilder();

    // ✅ GAME
    int secret;
    int lives = 5;

    boolean gameOver = false;
    boolean playerTurn = true;

    CountDownTimer timer;

    // ✅ AI
    Random random = new Random();

    int aiMin = 0;
    int aiMax = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // ✅ FIND VIEWS
        turnText = findViewById(R.id.turnText);
        resultText = findViewById(R.id.resultText);
        timerText = findViewById(R.id.timerText);
        inputDisplay = findViewById(R.id.inputDisplay);

        keypadContainer = findViewById(R.id.keypadContainer);

        // ✅ RANDOM SECRET
        secret = random.nextInt(101);

        // ✅ SETUP
        setupKeypad();

        // ✅ START GAME
        startPlayerTurn();
    }

    // ✅ PLAYER TURN
    private void startPlayerTurn() {

        if (gameOver)
            return;

        playerTurn = true;

        keypadContainer.setVisibility(View.VISIBLE);

        turnText.setText(
                "Your Turn - Lives: " + lives
        );

        resultText.setText("");

        setKeypadEnabled(true);

        startTimer();
    }

    // ✅ AI TURN
    private void startAITurn() {

        if (gameOver)
            return;

        playerTurn = false;

        keypadContainer.setVisibility(View.GONE);

        turnText.setText("AI Thinking...");

        cancelTimer();

        new Handler().postDelayed(() -> {

            if (gameOver)
                return;

            int aiGuess = (aiMin + aiMax) / 2;

            if (aiGuess < secret) {

                resultText.setText(
                        "AI guessed "
                                + aiGuess
                                + " ⬆ Higher"
                );

                aiMin = aiGuess + 1;

                startPlayerTurn();

            } else if (aiGuess > secret) {

                resultText.setText(
                        "AI guessed "
                                + aiGuess
                                + " ⬇ Lower"
                );

                aiMax = aiGuess - 1;

                startPlayerTurn();

            } else {

                resultText.setText(
                        "AI guessed "
                                + aiGuess
                                + " ✅ Correct!"
                );

                turnText.setText(
                        "AI Won 🎉"
                );

                keypadContainer.setVisibility(
                        View.GONE
                );
                cancelTimer();
                gameOver = true;
            }

        }, 1500);
    }

    // ✅ KEYPAD
    private void setupKeypad() {

        int[] buttons = {
                R.id.btn0, R.id.btn1, R.id.btn2,
                R.id.btn3, R.id.btn4, R.id.btn5,
                R.id.btn6, R.id.btn7, R.id.btn8,
                R.id.btn9
        };

        for (int id : buttons) {

            Button btn = findViewById(id);

            btn.setOnClickListener(v -> {
                if (gameOver || !playerTurn)
                  return;
                // ✅ CLICK ANIMATION
                v.animate()
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setDuration(50)
                        .withEndAction(() ->
                                v.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .start()
                        );

                // ✅ INPUT LIMIT
                if (inputVal.length() < 3) {

                    inputVal.append(
                            btn.getText()
                    );

                    inputDisplay.setText(
                            inputVal.toString()
                    );
                }
            });
        }

        // ✅ DELETE BUTTON
        Button delBtn = findViewById(R.id.btnDel);

        delBtn.setOnClickListener(v -> {

            if (gameOver || !playerTurn)
                return;

            if (inputVal.length() > 0) {

                inputVal.deleteCharAt(
                        inputVal.length() - 1
                );

                if (inputVal.length() == 0) {

                    inputDisplay.setText("0");

                } else {

                    inputDisplay.setText(
                            inputVal.toString()
                    );
                }
            }
        });

        // ✅ OK BUTTON
        Button okBtn = findViewById(R.id.btnOk);

        okBtn.setOnClickListener(v -> {

            if (gameOver || !playerTurn)
                return;

            if (inputVal.length() == 0)
                return;

            int guess = Integer.parseInt(
                    inputVal.toString()
            );

            // ✅ RESET INPUT
            inputVal.setLength(0);

            inputDisplay.setText("0");

            // ✅ CHECK GUESS
            checkPlayerGuess(guess);
        });
    }

    // ✅ CHECK PLAYER GUESS
    private void checkPlayerGuess(int guess) {

        cancelTimer();

        setKeypadEnabled(false);

        if (guess < secret) {

            resultText.setText("⬆ Higher");

            lives--;

        } else if (guess > secret) {

            resultText.setText("⬇ Lower");

            lives--;

        } else {

            resultText.setText("🎉 Correct!");

            turnText.setText("You Won!");

            keypadContainer.setVisibility(
                    View.GONE
            );
            cancelTimer();
            gameOver = true;

            return;
        }

        // ✅ PLAYER LOST
        if (lives <= 0) {

            resultText.setText("💀 You Lost!");

            turnText.setText("Game Over");

            keypadContainer.setVisibility(
                    View.GONE
            );
            cancelTimer();
            gameOver = true;

            return;
        }

        // ✅ AI TURN
        startAITurn();
    }

    // ✅ ENABLE / DISABLE KEYPAD
    private void setKeypadEnabled(boolean enabled) {

        int[] buttons = {
                R.id.btn0, R.id.btn1, R.id.btn2,
                R.id.btn3, R.id.btn4, R.id.btn5,
                R.id.btn6, R.id.btn7, R.id.btn8,
                R.id.btn9,
                R.id.btnDel,
                R.id.btnOk
        };

        for (int id : buttons) {

            View v = findViewById(id);

            if (v != null) {
                v.setEnabled(enabled);
            }
        }
    }

    // ✅ TIMER
    private void startTimer() {

        cancelTimer();

        timer = new CountDownTimer(
                15000,
                1000
        ) {

            @Override
            public void onTick(long millisUntilFinished) {

                int sec = (int)
                        (millisUntilFinished / 1000);

                timerText.setText(
                        "⏳ " + sec + "s"
                );
            }

            @Override
            public void onFinish() {

                if (gameOver)
                    return;

                lives--;

                turnText.setText(
                        "Lives: " + lives
                );

                // ✅ PLAYER LOST
                if (lives <= 0) {

                    resultText.setText(
                            "💀 You Lost!"
                    );

                    turnText.setText(
                            "Game Over"
                    );

                    keypadContainer.setVisibility(
                            View.GONE
                    );
                    cancelTimer();
                    gameOver = true;

                } else {

                    resultText.setText(
                            "⏰ Time Up!"
                    );

                    startAITurn();
                }
            }
        };

        timer.start();
    }

    // ✅ CANCEL TIMER
    private void cancelTimer() {

        if (timer != null) {
            timer.cancel();
        }
    }

    // ✅ CLEANUP
    @Override
    protected void onDestroy() {
        super.onDestroy();

        gameOver = true;

        cancelTimer();
    }
}
