package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class SetupActivity extends Activity {

    int setter, scoreP1, scoreP2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        EditText input = findViewById(R.id.secretInput);
        Button btn = findViewById(R.id.startDuelBtn);
        TextView title = findViewById(R.id.titleText);

        setter = getIntent().getIntExtra("setter", 1);
        scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        scoreP2 = getIntent().getIntExtra("scoreP2", 0);

        title.setText("Player " + setter + " set number");
        btn.setText("Set ✅");

        btn.setOnClickListener(v -> {

            String text = input.getText().toString();
            if (text.isEmpty()) return;

            try {
                int secret = Integer.parseInt(text);

                Intent i = new Intent(this, GameActivity.class);
                i.putExtra("secret", secret);
                i.putExtra("setter", setter);
                i.putExtra("scoreP1", scoreP1);
                i.putExtra("scoreP2", scoreP2);

                startActivity(i);

            } catch (Exception ignored) {}
        });
    }
}
