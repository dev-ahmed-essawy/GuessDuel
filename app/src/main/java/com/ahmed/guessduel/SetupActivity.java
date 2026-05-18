package com.ahmed.guessduel;

import android.content.Intent;import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class SetupActivity extends Activity {

    TextView titleText;
    TextView inputDisplay;

    LinearLayout keypadContainer;

    StringBuilder inputVal = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // ✅ VIEWS
        titleText = findViewById(R.id.titleText);
        inputDisplay = findViewById(R.id.inputDisplay);

        keypadContainer = findViewById(R.id.keypadContainer);

        titleText.setText("Set Secret Number");

        setupKeypad();
    }

    // ✅ KEYPAD SETUP
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

                // ✅ BUTTON ANIMATION
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
                            b.getText()
                    );

                    inputDisplay.setText(
                            inputVal.toString()
                    );
                }
            });
        }

        // ✅ DELETE BUTTON
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

            int secret = Integer.parseInt(
                    inputVal.toString()
            );

            // ✅ VALIDATION
            if (secret < 0 || secret > 100) {

                inputDisplay.setText("0-100");

                return;
            }

            // ✅ SEND SECRET TO CLIENT
            NetworkManager.send(
                    "SECRET:" + secret
            );

            // ✅ OPEN GAME AS HOST
            Intent i = new Intent(
                    SetupActivity.this,
                    GameActivity.class
            );

            i.putExtra("isHost", true);
            i.putExtra("secret", secret);

            startActivity(i);

            finish();
        });
    }
}
