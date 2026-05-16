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
        Button start = findViewById(R.id.startDuelBtn);

        start.setOnClickListener(v -> {

            String text = input.getText().toString();

            if (text.isEmpty()) return;

            try {
                int secret = Integer.parseInt(text);

                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("secret", secret);

                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
