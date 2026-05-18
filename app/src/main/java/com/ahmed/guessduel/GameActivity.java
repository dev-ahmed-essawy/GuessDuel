
package com.ahmed.guessduel;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends Activity {

    // ✅ UI
    TextView turnText;
    TextView resultText;
    TextView timerText;
    TextView inputDisplay;

    LinearLayout keypadContainer;

    // ✅ INPUT
    StringBuilder inputVal = new StringBuilder();

    // ✅ GAME STATE
    boolean isHost = false;
    boolean gameOver = false;

    int secret = -1;
    int lives = 5;

    CountDownTimer timer;
    
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

        // ✅ ROLE
        isHost = getIntent().getBooleanExtra(
                "isHost",
                false
        );

        // ✅ HOST MODE
        if (isHost) {

            secret = getIntent().getIntExtra(
                    "secret",
                    -1
            );

            keypadContainer.setVisibility(View.GONE);

            inputDisplay.setVisibility(View.GONE);

            timerText.setVisibility(View.GONE);

            turnText.setText(
                    "Waiting for guesses..."
            );

            resultText.setText("");

            startHostListener();
        }

        // ✅ CLIENT MODE
        else {

            keypadContainer.setVisibility(View.VISIBLE);

            turnText.setText(
                    "Lives: " + lives
            );

            resultText.setText(
                    "Guess the number!"
            );

            setupKeypad();

            startClientListener();

            startTimer();
        }
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

        // ✅ OK BUTTON
        Button okBtn = findViewById(R.id.btnOk);

        okBtn.setOnClickListener(v -> {

            if (gameOver)
                return;

            if (inputVal.length() == 0)
                return;

            int guess = Integer.parseInt(
                    inputVal.toString()
            );

            // ✅ SEND GUESS
            NetworkManager.send(
                    "GUESS:" + guess
            );

            resultText.setText(
                    "Guess sent: " + guess
            );

            // ✅ RESET INPUT
            inputVal.setLength(0);

            inputDisplay.setText("0");

            // ✅ STOP TIMER WHILE WAITING
            cancelTimer();

            // ✅ DISABLE KEYPAD
            setKeypadEnabled(false);
        });
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

    // ✅ HOST LISTENER
    private void startHostListener() {

        new Thread(() -> {

            while (!gameOver) {

                try {

                    String msg = NetworkManager.receive();

                    if (msg == null)
                        continue;

                    // ✅ CLIENT LOST
                    if (msg.equals("LOSE")) {

                        runOnUiThread(() -> {

                            turnText.setText(
                                    "Player Lost 💀"
                            );

                            resultText.setText(
                                    "Game Over"
                            );

                            gameOver = true;
                        });

                        continue;
                    }

                    // ✅ RECEIVE GUESS
                    if (msg.startsWith("GUESS:")) {

                        int guess = Integer.parseInt(
                                msg.replace(
                                        "GUESS:",
                                        ""
                                )
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
                                    "Player guessed: "
                                            + guess
                            );

                            if (finalResult.equals("WIN")) {

                                turnText.setText(
                                        "Player Won 🎉"
                                );

                                gameOver = true;
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

            while (!gameOver) {

                try {

                    String msg = NetworkManager.receive();

                    if (msg == null)
                        continue;

                    // ✅ RECEIVE RESULT
                    if (msg.startsWith("RESULT:")) {

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

                                    lives--;

                                    break;

                                case "LOWER":

                                    resultText.setText(
                                            "⬇ Lower"
                                    );

                                    lives--;

                                    break;

                                case "WIN":

                                    resultText.setText(
                                            "🎉 Correct!"
                                    );

                                    turnText.setText(
                                            "You Won!"
                                    );

                                    keypadContainer.setVisibility(
                                            View.GONE
                                    );

                                    cancelTimer();

                                    gameOver = true;

                                    return;
                            }

                            // ✅ UPDATE LIVES
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

                                // ✅ INFORM HOST
                                NetworkManager.send("LOSE");

                                cancelTimer();

                                gameOver = true;

                                return;
                            }

                            // ✅ ENABLE INPUT AGAIN
                            setKeypadEnabled(true);

                            // ✅ RESTART TIMER
                            startTimer();
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

                    // ✅ INFORM HOST
                    NetworkManager.send("LOSE");

                    cancelTimer();

                    gameOver = true;

                } else {

                    resultText.setText(
                            "⏰ Time Up!"
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

        gameOver = true;

        cancelTimer();
    }
}
