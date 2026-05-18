package com.ahmed.guessduel;

import android.app.Activity;
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

    int lives = 5;
    int timerSeconds = 15;

    boolean gameOver = false;

    // ✅ PHASES
    boolean playerSetting = true;
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

        // ✅ SETUP KEYPAD
        setupKeypad();

        // ✅ START FIRST PHASE
        startPlayerSetPhase();
    }

    // ✅ PLAYER SETS SECRET
    private void startPlayerSetPhase() {

        playerSetting = true;
        playerGuessing = false;
        aiGuessing = false;

        inputVal.setLength(0);

        inputDisplay.setText("0");

        keypadContainer.setVisibility(View.VISIBLE);

        setKeypadEnabled(true);

        timerText.setText("");

        turnText.setText(
                "Set Your Secret Number"
        );

        resultText.setText(
                "AI will try to guess it"
        );

        cancelTimer();
    }

    // ✅ AI GUESSES PLAYER NUMBER
    private void startAIGuessPhase() {

        if (gameOver)
            return;

        playerSetting = false;
        playerGuessing = false;
        aiGuessing = true;

        keypadContainer.setVisibility(View.GONE);

        turnText.setText("AI Guessing...");

        startTimer();

        new Handler().postDelayed(() -> {

            if (gameOver)
                return;

            int aiGuess = (aiMin + aiMax) / 2;

            cancelTimer();

            if (aiGuess < secret) {

                resultText.setText(
                        "AI guessed "
                                + aiGuess
                                + " ⬆ Higher"
                );

                aiMin = aiGuess + 1;

                startAIGuessPhase();

            } else if (aiGuess > secret) {

                resultText.setText(
                        "AI guessed "
                                + aiGuess
                                + " ⬇ Lower"
                );

                aiMax = aiGuess - 1;

                startAIGuessPhase();

            } else {

                resultText.setText(
                        "AI guessed "
                                + aiGuess
                                + " ✅ Correct!"
                );

                turnText.setText(
                        "AI Finished!"
                );

                // ✅ NEXT ROUND
                new Handler().postDelayed(() -> {

                    startPlayerGuessPhase();

                }, 2000);
            }

        }, 1500);
    }

    // ✅ AI SETS SECRET
    private void startPlayerGuessPhase() {

        if (gameOver)
            return;

        playerSetting = false;
        playerGuessing = true;
        aiGuessing = false;

        // ✅ AI SECRET
        secret = random.nextInt(101);

        inputVal.setLength(0);

        inputDisplay.setText("0");

        keypadContainer.setVisibility(View.VISIBLE);

        setKeypadEnabled(true);

        turnText.setText(
                "Guess AI Number - Lives: " + lives
        );

        resultText.setText("");

        startTimer();
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

            // ✅ PLAYER SET PHASE
            if (playerSetting) {

                secret = value;

                aiMin = 0;
                aiMax = 100;

                startAIGuessPhase();

                return;
            }

            // ✅ PLAYER GUESS PHASE
            if (playerGuessing) {

                checkPlayerGuess(value);
            }
        });
    }

    // ✅ PLAYER GUESSES AI NUMBER
    private void checkPlayerGuess(int guess) {

        cancelTimer();

        if (guess < secret) {

            resultText.setText("⬆ Higher");

            lives--;

        } else if (guess > secret) {

            resultText.setText("⬇ Lower");

            lives--;

        } else {

            resultText.setText(
                    "🎉 You Won!"
            );

            turnText.setText(
                    "Game Over"
            );

            keypadContainer.setVisibility(
                    View.GONE
            );

            cancelTimer();

            gameOver = true;

            return;
        }

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

            return;
        }

        turnText.setText(
                "Guess AI Number - Lives: "
                        + lives
        );

        startTimer();
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

                lives--;

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

                    turnText.setText(
                            "Lives: " + lives
                    );

                    resultText.setText(
                            "⏰ Time Up!"
                    );

                    // ✅ AI PHASE CONTINUES
                    if (aiGuessing) {

                        startAIGuessPhase();

                    } else if (playerGuessing) {

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
