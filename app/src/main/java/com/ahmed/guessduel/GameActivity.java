package com.ahmed.guessduel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class GameActivity extends Activity {

    int secret = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        EditText input = findViewById(R.id.input);
        TextView result = findViewById(R.id.resultText);
        Button btn = findViewById(R.id.submitBtn);

        btn.setOnClickListener(v -> {
            String text = input.getText().toString();

            if (text.isEmpty()) {
                result.setText("Enter a number!");
                return;
            }

            try {
                int g = Integer.parseInt(text);

                if (g < secret) {
                    result.setText("⬆ Higher");
                } else if (g > secret) {
                    result.setText("⬇ Lower");
                } else {
                    result.setText("✅ Correct!");
                }

            } catch (Exception e) {
                result.setText("Invalid input!");
            }
        });
    }
}
