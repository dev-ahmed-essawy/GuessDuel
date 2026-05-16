
package com.essawy.guessduel;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

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

        if (g < secret) result.setText("⬆ Higher");
        else if (g > secret) result.setText("⬇ Lower");
        else result.setText("✅ Correct!");

    } catch (Exception e) {
        result.setText("Invalid input!");
    }
});

    }
}
