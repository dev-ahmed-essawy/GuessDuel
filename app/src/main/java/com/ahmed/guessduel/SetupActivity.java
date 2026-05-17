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

        StringBuilder inputVal = new StringBuilder();

        int setter = getIntent().getIntExtra("setter", 1);
        int timer = getIntent().getIntExtra("timer", 15);

        String name1 = getIntent().getStringExtra("name1");
        String name2 = getIntent().getStringExtra("name2");

        int scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        int scoreP2 = getIntent().getIntExtra("scoreP2", 0);

        if (name1 == null) name1 = "Player 1";
        if (name2 == null) name2 = "Player 2";

        title.setText((setter == 1 ? name1 : name2) + " sets number");

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

        findViewById(R.id.btnDel).setOnClickListener(v -> {
            if (inputVal.length() > 0) {
                inputVal.deleteCharAt(inputVal.length() - 1);
                display.setText(inputVal.length()==0 ? "0" : inputVal.toString());
            }
        });

        findViewById(R.id.btnOk).setOnClickListener(v -> {

            if (inputVal.length() == 0) return;

            int secret = Integer.parseInt(inputVal.toString());

            if (secret < 0 || secret > 100) {
                display.setText("0-100!");
                return;
            }

            Intent i = new Intent(this, GameActivity.class);
            i.putExtra("secret", secret);
            i.putExtra("setter", setter);
            i.putExtra("timer", timer);
            i.putExtra("name1", name1);
            i.putExtra("name2", name2);
            i.putExtra("scoreP1", scoreP1);
            i.putExtra("scoreP2", scoreP2);

            startActivity(i);
        });
    }
}
