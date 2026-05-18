package com.ahmed.guessduel;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.*;

public class GameActivity extends Activity {

    TextView turnText;
    TextView resultText;
    TextView timerText;
    TextView inputDisplay;

    LinearLayout keypadContainer;

    StringBuilder inputVal = new StringBuilder();

    boolean isHost = false;

    int secret = -1;
    int lives = 5;

    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // ✅ VIEWS
        turnText = findViewById(R.id.turnText);
        resultText = findViewById(R.id.resultText);
        timerText = findViewById(R.id.timerText);
        inputDisplay = findViewById(R.id.inputDisplay);

        keypadContainer = findViewById(R.id.keypadContainer);

        // ✅ ROLE
        isHost = getIntent().getBooleanExtra("isHost", false);

        // ✅ HOST MODE
        if (isHost) {

            keypadContainer.setVisibility(View.GONE);

            turnText.setText("Waiting for guesses...");

            secret = getIntent().getIntExtra("secret", -1);

            startHostListener();

        }

        // ✅ CLIENT MODE
        else {

            turnText.setText("Guess the number!");

            keypadContainer.setVisibility(View.VISIBLE);

            setupKeypad();

            startTimer();
        }
    }

    // ✅ CLIENT KEYPAD
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

                // ✅ animation
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

            if (inputVal.length() == 0)
                return;

            int guess = Integer.parseInt(
                    inputVal.toString()
            );

            // ✅ SEND GUESS
            NetworkManager.send(
                    "GUESS:" + guess
            );

            inputVal.setLength(0);

            inputDisplay.setText("0");
        });

        // ✅ LISTEN FOR RESULTS
        startClientListener();
    }

    // ✅ HOST LISTENER
    private void startHostListener() {

        new Thread(() -> {

            while (true) {

                try {

                    String msg = NetworkManager.receive();

                    if (msg != null &&
                            msg.startsWith("GUESS:")) {

                        int guess = Integer.parseInt(
                                msg.replace("GUESS:", "")
                        );

                        String result;

                        if (guess < secret) {

                            result = "HIGHER";

                        } else if (guess > secret) {

                            result = "LOWER";

                        } else {

                            result = "WIN";
                        }

                        // ✅ SEND RESULT
                        NetworkManager.send(
                                "RESULT:" + result
                        );

                        String finalResult = result;

                        runOnUiThread(() -> {

                            resultText.setText(
                                    "Player guessed: " + guess
                            );

                            if (finalResult.equals("WIN")) {

                                turnText.setText(
                                        "Player Won 🎉"
                                );
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    // ✅ CLIENT LISTENER
    private void startClientListener() {

        new Thread(() -> {

            while (true) {

                try {

                    String msg = NetworkManager.receive();

                    if (msg != null &&
                            msg.startsWith("RESULT:")) {

                        String result = msg.replace(
                                "RESULT:",
                                ""
                        );

                        runOnUiThread(() -> {

                            switch (result) {

                                case "HIGHER":

                                    resultText.setText(
                                            "⬆ Higher"
                                    );

                                    break;

                                case "LOWER":

                                    resultText.setText(
                                            "⬇ Lower"
                                    );

                                    break;

                                case "WIN":

                                    resultText.setText(
                                            "🎉 Correct!"
                                    );

                                    keypadContainer.setVisibility(
                                            View.GONE
                                    );

                                    cancelTimer();

                                    break;
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    // ✅ TIMER
    private void startTimer() {

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

                lives--;

                if (lives <= 0) {

                    resultText.setText(
                            "💀 You Lost!"
                    );

                    keypadContainer.setVisibility(
                            View.GONE
                    );

                } else {

                    resultText.setText(
                            "⏰ Time up!"
                    );

                    turnText.setText(
                            "Lives: " + lives
                    );

                    startTimer();
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

        cancelTimer();
    }
}
