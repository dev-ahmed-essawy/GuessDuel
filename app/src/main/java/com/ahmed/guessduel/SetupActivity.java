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

        EditText input = findViewById(R.id.secretInput);
        Button btn = findViewById(R.id.startDuelBtn);
        TextView title = findViewById(R.id.titleText);

        int setter = getIntent().getIntExtra("setter", 1);
        int timer = getIntent().getIntExtra("timer", 15);

        String name1 = getIntent().getStringExtra("name1");
        String name2 = getIntent().getStringExtra("name2");

        int scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        int scoreP2 = getIntent().getIntExtra("scoreP2", 0);

        if (name1 == null) name1 = "Player 1";
        if (name2 == null) name2 = "Player 2";

        // ✅ Detect AI mode
        boolean isAI = name2.equals("AI");

        // ✅ FIX: In AI mode → player 1 always sets the number
        String setterName;
        if (isAI) {
            setter = 1;
            setterName = name1;
        } else {
            setterName = (setter == 1) ? name1 : name2;
        }

        title.setText(setterName + " sets number");

        btn.setOnClickListener(v -> {

            String text = input.getText().toString().trim();

            // ✅ Empty check
            if (text.isEmpty()) {
                input.setError("Enter a number!");
                return;
            }

            int secret;

            // ✅ Safe parsing
            try {
                secret = Integer.parseInt(text);
            } catch (Exception e) {
                input.setError("Invalid number!");
                return;
            }

            // ✅ Range check
            if (secret < 0 || secret > 100) {
                input.setError("0 - 100 only");
                return;
            }

            // ✅ Move to GameActivity
            Intent i = new Intent(SetupActivity.this, GameActivity.class);

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
