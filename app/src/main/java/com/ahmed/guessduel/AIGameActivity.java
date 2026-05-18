package com.ahmed.guessduel;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.*;

public class AIGameActivity extends Activity {

    TextView turnText;
    TextView resultText;
    TextView timerText;
    TextView inputDisplay;

    LinearLayout keypadContainer;

    StringBuilder inputVal = new StringBuilder();

    int secret = 0;
    int lives = 5;

    CountDownTimer timer;

    int aiMin = 0;
    int aiMax = 100;

    boolean playerTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        turnText = findViewById(R.id.turnText);
        resultText = findViewById(R.id.resultText);
        timerText = findViewById(R.id.timerText);
        inputDisplay = findViewById(R.id.inputDisplay);

        keypadContainer = findViewById(R.id.keypadContainer);

        // ✅ RANDOM SECRET
        secret = (int)(Math.random() * 101);

        setupKeypad();

        startPlayerTurn();
    }

    // ✅ PLAYER TURN
    private void startPlayerTurn() {

        playerTurn = true;

        keypadContainer.setVisibility(View.VISIBLE);

        turnText.setText("Your Turn");
        resultText.setText("");

        startTimer();
    }

    // ✅ AI TURN
    private void startAITurn() {

        playerTurn = false;

        keypadContainer.setVisibility(View.GONE);

        turnText.setText("AI Thinking...");

        cancelTimer();

        new android.os.Handler().postDelayed(() -> {

            int aiGuess = (aiMin + aiMax) / 2;

            if (aiGuess < secret) {

                resultText.setText(
                        "AI guessed " + aiGuess + " ⬆"
                );

                aiMin = aiGuess + 1;

                startPlayerTurn();

            } else if (aiGuess > secret) {

                resultText.setText(
                        "AI guessed " + aiGuess + " ⬇"
                );

                aiMax = aiGuess - 1;

                startPlayerTurn();

            } else {

                resultText.setText(
                        "AI WON 🎉"
                );

                turnText.setText("Game Over");

                keypadContainer.setVisibility(View.GONE);
            }

        }, 1500);
    }

    // ✅ KEYPAD
    private void setupKeypad() {

        int[] btns = {
                R.id.btn0, R.id.btn1, R.id.btn2,
                R.id.btn3, R.id.btn4, R.id.btn5,
                R.id.btn6, R.id.btn7, R.id.btn8,
                R.id.btn9
        };

        for (int id : btns) {

            Button b = findViewById(id);

            b.setOnClickListener(v -> {

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

                    inputVal.append(b.getText());

                    inputDisplay.setText(
                            inputVal.toString()
                    );
                }
            });
        }

        // ✅ DELETE
        findViewById(R.id.btnDel).setOnClickListener(v -> {

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
        findViewById(R.id.btnOk).setOnClickListener(v -> {

            if (!playerTurn)
                return;

            if (inputVal.length() == 0)
