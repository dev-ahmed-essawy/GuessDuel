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
    int secret = -1;

    int timerSeconds = 15;

    int currentLives = 5;

    int playerScore = 0;
    int aiScore = 0;

    boolean gameOver = false;

    // ✅ PHASES
    boolean playerSetting = false;
    boolean playerGuessing = false;
    boolean aiGuessing = false;

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

        // ✅ TIMER FROM DIFFICULTY
        timerSeconds = getIntent().getIntExtra(
                "timer",
                15
        );

        // ✅ SETUP
        setupKeypad();

        // ✅ START FIRST ROUND
        startPlayerSetPhase();
    }

    // ✅ PLAYER SETS SECRET
    private void startPlayerSetPhase() {

        playerSetting = true;
        playerGuessing = false;
        aiGuessing = false;

        currentLives = 5;

        inputVal.setLength(0);

        inputDisplay.setText("0");

        keypadContainer.setVisibility(
                View.VISIBLE
        );

        timerText.setText("");

        turnText.setText(
                "Set Number For AI"
        );

        resultText.setText(
                "Player: "
                        + playerScore
                        + " | AI: "
                        + aiScore
        );

        cancelTimer();
    }

    // ✅ AI GUESSES
    private void startAIGuessPhase() {

        if (gameOver)
            return;

        playerSetting = false;
        playerGuessing = false;
        aiGuessing = true;

        keypadContainer.setVisibility(
                View.GONE
        );

        turnText.setText(
                "AI Guessing - Lives: "
                        + currentLives
        );

        startTimer();

        new Handler().postDelayed(() -> {

            if (gameOver)
                return;

            cancelTimer();

            int aiGuess = (aiMin + aiMax) / 2;

            if (aiGuess < secret) {

                resultText.setText(
                        "AI guessed "
                                + aiGuess
                                + " ⬆ Higher"
                );

                aiMin = aiGuess + 1;

                currentLives--;

            } else if (aiGuess > secret) {

                resultText.setText(
                        "AI guessed "
                                + aiGuess
                                + " ⬇ Lower"
                );

                aiMax = aiGuess - 1;

                currentLives--;

            } else {

                resultText.setText(
                        "AI guessed "
                                + aiGuess
                                + " ✅ Correct!"
                );

                aiScore++;

                checkMatchWinner();

                if (gameOver)
                    return;

                new Handler().postDelayed(() -> {

                    startPlayerGuessPhase();

                }, 2000);

                return;
            }

            // ✅ AI LOST ROUND
            if (currentLives <= 0) {

                resultText.setText(
                        "AI Failed 💀"
                );

                playerScore++;

                checkMatchWinner();

                if (gameOver)
                    return;

                new Handler().postDelayed(() -> {

                    startPlayerGuessPhase();

                }, 2000);

                return;
            }

            turnText.setText(
                    "AI Guessing - Lives: "
                            + currentLives
            );

            startAIGuessPhase();

        }, 1500);
    }

    // ✅ AI SETS SECRET
    private void startPlayerGuessPhase() {

        if (gameOver)
            return;

        playerSetting = false;
        playerGuessing = true;
        aiGuessing = false;

        currentLives = 5;

        // ✅ AI SECRET
        secret = random.nextInt(101);

        inputVal.setLength(0);

        inputDisplay.setText("0");

        keypadContainer.setVisibility(
                View.VISIBLE
        );

        timerText.setText("");

        turnText.setText(
                "Guess AI Number - Lives: "
                        + currentLives
        );

        resultText.setText(
                "Player: "
                        + playerScore
                        + " | AI: "
                        + aiScore
        );
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

                if (gameOver)
                    return;

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

        // ✅ DELETE
        Button delBtn = findViewById(R.id.btnDel);

        delBtn.setOnClickListener(v -> {

            if (gameOver)
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

        // ✅ OK
        Button okBtn = findViewById(R.id.btnOk);

        okBtn.setOnClickListener(v -> {

            if (gameOver)
                return;

            if (inputVal.length() == 0)
                return;

            int value = Integer.parseInt(
                    inputVal.toString()
            );

            inputVal.setLength(0);

            inputDisplay.setText("0");

            // ✅ PLAYER SETS
            if (playerSetting) {

                secret = value;

                aiMin = 0;
                aiMax = 100;

                startAIGuessPhase();

                return;
            }

            // ✅ PLAYER GUESSES
            if (playerGuessing) {

                checkPlayerGuess(value);
            }
        });
    }

    // ✅ PLAYER GUESSES AI NUMBER
    private void checkPlayerGuess(int guess) {

        startTimer();

        if (guess < secret) {

            resultText.setText(
                    "⬆ Higher"
            );

            currentLives--;

        } else if (guess > secret) {

            resultText.setText(
                    "⬇ Lower"
            );

            currentLives--;

        } else {

            resultText.setText(
                    "🎉 Correct!"
            );

            playerScore++;

            checkMatchWinner();

            if (gameOver)
                return;

            new Handler().postDelayed(() -> {

                startPlayerSetPhase();

            }, 2000);

            return;
        }

        // ✅ PLAYER LOST ROUND
        if (currentLives <= 0) {

            resultText.setText(
                    "💀 You Failed!"
            );

            aiScore++;

            checkMatchWinner();

            if (gameOver)
                return;

            new Handler().postDelayed(() -> {

                startPlayerSetPhase();

            }, 2000);

            return;
        }

        turnText.setText(
                "Guess AI Number - Lives: "
                        + currentLives
        );
    }

    // ✅ CHECK FINAL WINNER
    private void checkMatchWinner() {

        if (playerScore >= 5) {

            openResultScreen(
                    "PLAYER WON!"
            );

        } else if (aiScore >= 5) {

            openResultScreen(
                    "AI WON!"
            );
        }
    }

    // ✅ RESULT SCREEN
    private void openResultScreen(String winner) {

        gameOver = true;

        cancelTimer();

        Intent i = new Intent(
                AIGameActivity.this,
                ResultActivity.class
        );

        i.putExtra(
                "winner",
                winner
        );

        i.putExtra(
                "playerScore",
                playerScore
        );

        i.putExtra(
                "aiScore",
                aiScore
        );

        startActivity(i);

        finish();
    }

    // ✅ TIMER
    private void startTimer() {

        cancelTimer();

        timer = new CountDownTimer(
                timerSeconds * 1000L,
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

                currentLives--;

                // ✅ AI TURN
                if (aiGuessing) {

                    resultText.setText(
                            "⏰ AI Time Up!"
                    );

                    if (currentLives <= 0) {

                        playerScore++;

                        checkMatchWinner();

                        if (gameOver)
                            return;

                        startPlayerGuessPhase();

                    } else {

                        turnText.setText(
                                "AI Guessing - Lives: "
                                        + currentLives
                        );

                        startAIGuessPhase();
                    }
                }

                // ✅ PLAYER TURN
                else if (playerGuessing) {

                    resultText.setText(
                            "⏰ Your Time Up!"
                    );

                    if (currentLives <= 0) {

                        aiScore++;

                        checkMatchWinner();

                        if (gameOver)
                            return;

                        startPlayerSetPhase();

                    } else {

                        turnText.setText(
                                "Guess AI Number - Lives: "
                                        + currentLives
                        );

                        startTimer();
                    }
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
