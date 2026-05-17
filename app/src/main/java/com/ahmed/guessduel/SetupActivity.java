package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class SetupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        TextView title = findViewById(R.id.titleText);
        TextView display = findViewById(R.id.inputDisplay);
        LinearLayout keypadContainer = findViewById(R.id.keypadContainer);

        StringBuilder inputVal = new StringBuilder();

        int setter = getIntent().getIntExtra("setter", 1);
        int timer = getIntent().getIntExtra("timer", 15);

        String name1 = getIntent().getStringExtra("name1");
        String name2 = getIntent().getStringExtra("name2");

        int scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        int scoreP2 = getIntent().getIntExtra("scoreP2", 0);

        if (name1 == null) name1 = "Player 1";
        if (name2 == null) name2 = "Player 2";

        // ✅ FINAL copies
        final String finalName1 = name1;
        final String finalName2 = name2;
        final int finalSetter = setter;
        final int finalScoreP1 = scoreP1;
        final int finalScoreP2 = scoreP2;
        final int finalTimer = timer;

        // ✅ DETECT AI MODE
        boolean isAI = finalName2.equals("AI");

        // ✅ ✅ ✅ AI SETTER LOGIC (THIS WAS MISSING)
        if (isAI && finalSetter == 2) {

            title.setText("AI is setting...");
            display.setText("...");

            keypadContainer.setVisibility(LinearLayout.GONE); // hide keypad

            int aiSecret = (int)(Math.random() * 101);

            display.postDelayed(() -> {

                Intent i = new Intent(SetupActivity.this, GameActivity.class);

                i.putExtra("secret", aiSecret);
                i.putExtra("setter", 2);
                i.putExtra("timer", finalTimer);
                i.putExtra("name1", finalName1);
                i.putExtra("name2", finalName2);
                i.putExtra("scoreP1", finalScoreP1);
                i.putExtra("scoreP2", finalScoreP2);

                startActivity(i);
                finish();

            }, 1500);

            return; // ✅ STOP normal input
        }

        // ✅ NORMAL PLAYER SETTING
        title.setText((finalSetter == 1 ? finalName1 : finalName2) + " sets number");

        int[] btns = {
                R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,
                R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9
        };

        for (int id : btns) {
            Button b = findViewById(id);
            b.setOnClickListener(v -> {
                if (inputVal.length() < 3) {
                    inputVal.append(b.getText());
                    display.setText(inputVal.toString());
                }
            });
        }

        // DELETE
        findViewById(R.id.btnDel).setOnClickListener(v -> {
            if (inputVal.length() > 0) {
                inputVal.deleteCharAt(inputVal.length() - 1);
                display.setText(inputVal.length() == 0 ? "0" : inputVal.toString());
            }
        });

        // OK button
        findViewById(R.id.btnOk).setOnClickListener(v -> {

            if (inputVal.length() == 0) return;

            int secret = Integer.parseInt(inputVal.toString());

            if (secret < 0 || secret > 100) {
                display.setText("0-100!");
                return;
            }

            Intent i = new Intent(SetupActivity.this, GameActivity.class);

            i.putExtra("secret", secret);
            i.putExtra("setter", finalSetter);
            i.putExtra("timer", finalTimer);
            i.putExtra("name1", finalName1);
            i.putExtra("name2", finalName2);
            i.putExtra("scoreP1", finalScoreP1);
            i.putExtra("scoreP2", finalScoreP2);

            startActivity(i);
        });
    }
}
