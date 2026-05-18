package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
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

    // ✅ GAME
    boolean isHost = false;
    boolean gameOver = false;

    boolean settingPhase = false;
    boolean guessingPhase = false;

    boolean hostTurnToSet = true;

    int secret = -1;

    int currentLives = 5;

    int hostScore = 0;
    int clientScore = 0;

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

        // ✅ FIRST SECRET
        secret = getIntent().getIntExtra(
                "secret",
                -1
        );

        setupKeypad();

        // ✅ START LISTENERS
        if (isHost) {

            startHostListener();

            startHostSetterPhase();

        } else {

            startClientListener();

            startClientGuesserPhase();
        }
    }

    // ✅ HOST SETTER
    private void startHostSetterPhase() {

        settingPhase = true;
        guessingPhase = false;

        currentLives = 5;

        keypadContainer.setVisibility(
                View.GONE
        );

        inputDisplay.setVisibility(
                View.GONE
        );

        timerText.setVisibility(
                View.GONE
        );

        cancelTimer();

        turnText.setText(
                "Client Guessing..."
        );

        resultText.setText(
                "Host: "
                        + hostScore
                        + " | Client: "
                        + clientScore
        );
    }

    // ✅ HOST GUESSER
    private void startHostGuesserPhase() {

        settingPhase = false;
        guessingPhase = true;

        currentLives = 5;

        keypadContainer.setVisibility(
                View.VISIBLE
        );

        inputDisplay.setVisibility(
                View.VISIBLE
        );

        timerText.setVisibility(
                View.VISIBLE
        );

        setKeypadEnabled(true);

        turnText.setText(
                "Guess Number - Lives: "
                        + currentLives
        );

        resultText.setText(
                "Host: "
                        + hostScore
                        + " | Client: "
                        + clientScore
        );

        startTimer();
    }

    // ✅ CLIENT SETTER
    private void startClientSetterPhase() {

        settingPhase = true;
        guessingPhase = false;

        currentLives = 5;

        keypadContainer.setVisibility(
                View.VISIBLE
        );

        inputDisplay.setVisibility(
                View.VISIBLE
        );

        timerText.setVisibility(
                View.GONE
        );

        cancelTimer();

        inputVal.setLength(0);

        inputDisplay.setText("0");

        turnText.setText(
                "Set Secret Number"
        );

        resultText.setText(
                "Host: "
                        + hostScore
                        + " | Client: "
                        + clientScore
        );
    }

    // ✅ CLIENT GUESSER
    private void startClientGuesserPhase() {

        settingPhase = false;
        guessingPhase = true;

        currentLives = 5;

        keypadContainer.setVisibility(
                View.VISIBLE
        );

        inputDisplay.setVisibility(
                View.VISIBLE
        );

        timerText.setVisibility(
                View.VISIBLE
        );

        setKeypadEnabled(true);

        turnText.setText(
                "Guess Number - Lives: "
                        + currentLives
        );

        resultText.setText(
                "Host: "
                        + hostScore
                        + " | Client: "
                        + clientScore
        );

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
            

            // ✅ CLIENT SETS SECRET
            if (!isHost && settingPhase) {

                secret = value;

                NetworkManager.send(
                        "SET_SECRET:" + secret
                );

                inputVal.setLength(0);

                inputDisplay.setText("0");

                startClientSetterWaiting();

                return;
            }

            // ✅ HOST GUESSES
            // ✅ CLIENT GUESSES
            if (guessingPhase) {

                NetworkManager.send(
                        "GUESS:" + value
                );

                resultText.setText(
                        "Guess sent: " + value
                );

                inputVal.setLength(0);

                inputDisplay.setText("0");

                setKeypadEnabled(false);

                cancelTimer();
            }
        });
    }

    // ✅ CLIENT WAITING
    private void startClientSetterWaiting() {

        keypadContainer.setVisibility(
                View.GONE
        );

        inputDisplay.setVisibility(
                View.GONE
        );

        turnText.setText(
                "Host Guessing..."
        );

        resultText.setText(
                "Waiting for host guesses..."
        );
    }
    // ✅ HOST ENTERS NEW SECRET
      private void startHostSetterInputPhase() {

          settingPhase = true;
          guessingPhase = false;

          currentLives = 5;

          cancelTimer();

          // ✅ RESET INPUT
          inputVal.setLength(0);

          inputDisplay.setText("0");

          // ✅ SHOW INPUT UI
          keypadContainer.setVisibility(
                  View.VISIBLE
          );

          inputDisplay.setVisibility(
                  View.VISIBLE
          );

          timerText.setVisibility(
                  View.GONE
          );

          setKeypadEnabled(true);

          turnText.setText(
                  "Set Secret Number"
          );

          resultText.setText(
                  "Host: "
                          + hostScore
                          + " | Client: "
                          + clientScore
          );
      }
    

    // ✅ ENABLE / DISABLE
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

                    // ✅ RECEIVE SECRET
                    if (msg.startsWith("SET_SECRET:")) {

                        secret = Integer.parseInt(
                                msg.replace(
                                        "SET_SECRET:",
                                        ""
                                )
                        );

                        runOnUiThread(() -> {

                            startHostGuesserPhase();
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

                        NetworkManager.send(
                                "RESULT:" + result
                        );

                        String finalResult = result;

                        runOnUiThread(() -> {

                            resultText.setText(
                                    "Guess: " + guess
                            );

                            // ✅ CLIENT WON
                            if (finalResult.equals("WIN")) {

                                if (hostTurnToSet) {

                                    clientScore++;

                                } else {

                                    hostScore++;
                                }

                                nextRound();
                            }
                        });
                    }

                    // ✅ PLAYER LOST
                    if (msg.equals("LOSE")) {

                        if (hostTurnToSet) {

                            hostScore++;

                        } else {

                            clientScore++;
                        }

                        runOnUiThread(() -> {

                            nextRound();
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

                                    currentLives--;

                                    break;

                                case "LOWER":

                                    resultText.setText(
                                            "⬇ Lower"
                                    );

                                    currentLives--;

                                    break;

                                case "WIN":

                                    resultText.setText(
                                            "🎉 Correct!"
                                    );

                                    if (hostTurnToSet) {

                                        clientScore++;

                                    } else {

                                        hostScore++;
                                    }

                                    nextRound();

                                    return;
                            }

                            // ✅ LOST ROUND
                            if (currentLives <= 0) {

                                NetworkManager.send(
                                        "LOSE"
                                );

                                if (hostTurnToSet) {

                                    hostScore++;

                                } else {

                                    clientScore++;
                                }

                                return;
                            }

                            turnText.setText(
                                    "Lives: "
                                            + currentLives
                            );

                            setKeypadEnabled(true);

                            startTimer();
                        });
                    }

                    // ✅ NEXT ROUND
                    if (msg.equals("NEXT_ROUND")) {

                        runOnUiThread(() -> {

                            hostTurnToSet =
                                    !hostTurnToSet;

                            if (hostTurnToSet) {

                                startClientGuesserPhase();

                            } else {

                                startClientSetterPhase();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    // ✅ NEXT ROUND
    private void nextRound() {

        checkWinner();

        if (gameOver)
            return;

        hostTurnToSet = !hostTurnToSet;

        NetworkManager.send(
                "NEXT_ROUND"
        );

        if (isHost) {

            if (hostTurnToSet) {

                startHostSetterInputPhase();

            } else {

                startHostGuesserPhase();
            }

        } else {

            if (hostTurnToSet) {

                startClientGuesserPhase();

            } else {

                startClientSetterPhase();
            }
        }
    }

    // ✅ CHECK WINNER
    private void checkWinner() {

        if (hostScore >= 5) {

            openResultScreen(
                    "HOST WON!"
            );

        } else if (clientScore >= 5) {

            openResultScreen(
                    "CLIENT WON!"
            );
        }
    }

    // ✅ RESULT
    private void openResultScreen(String winner) {

        gameOver = true;

        cancelTimer();

        Intent i = new Intent(
                GameActivity.this,
                ResultActivity.class
        );

        i.putExtra(
                "winner",
                winner
        );

        i.putExtra(
                "hostScore",
                hostScore
        );

        i.putExtra(
                "clientScore",
                clientScore
        );

        startActivity(i);

        finish();
    }

    // ✅ TIMER
    private void startTimer() {

        cancelTimer();

        timer = new CountDownTimer(
                15000,
                1000
        ) {

            @Override
            public void onTick(
                    long millisUntilFinished
            ) {

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

                if (currentLives <= 0) {

                    NetworkManager.send(
                            "LOSE"
                    );

                    resultText.setText(
                            "💀 You Failed!"
                    );

                    nextRound();

                    return;
                }

                turnText.setText(
                        "Lives: "
                                + currentLives
                );

                resultText.setText(
                        "⏰ Time Up!"
                );

                startTimer();
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
