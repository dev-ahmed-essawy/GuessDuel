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

        // ✅ Detect AI
        boolean isAI = name2.equals("AI");

        // ✅ FIX: handle setter logic
        final int finalSetter;
        final String finalName1 = name1;
        final String finalName2 = name2;

        String setterName;

        if (isAI) {
            finalSetter = 1; // player always sets
            setterName = finalName1;
        } else {
            finalSetter = setter;
            setterName = (setter == 1) ? finalName1 : finalName2;
        }

        title.setText(setterName + " sets number");

        btn.setOnClickListener(v -> {

            String text = input.getText().toString().trim();

            if (text.isEmpty()) {
                input.setError("Enter a number!");
                return;
            }

            int secret;

            try {
                secret = Integer.parseInt(text);
            } catch (Exception e) {
                input.setError("Invalid number!");
                return;
            }

            if (secret < 0 || secret > 100) {
                input.setError("0 - 100 only");
                return;
            }

            Intent i = new Intent(SetupActivity.this, GameActivity.class);

            i.putExtra("secret", secret);
            i.putExtra("setter", finalSetter);   // ✅ FIXED
            i.putExtra("timer", timer);
            i.putExtra("name1", finalName1);     // ✅ FIXED
            i.putExtra("name2", finalName2);     // ✅ FIXED
            i.putExtra("scoreP1", scoreP1);
            i.putExtra("scoreP2", scoreP2);

            startActivity(i);
        });
    }
}
