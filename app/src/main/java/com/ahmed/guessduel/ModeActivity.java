package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ModeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        Button aiBtn = findViewById(R.id.btnAI);
        Button wifiBtn = findViewById(R.id.btnWifi);

        // ✅ SINGLE PLAYER (AI)
        aiBtn.setOnClickListener(v -> {

            Intent i = new Intent(
                    ModeActivity.this,
                    DifficultyActivity.class
            );

            startActivity(i);
        });

        // ✅ WIFI MULTIPLAYER
        wifiBtn.setOnClickListener(v -> {

            Intent i = new Intent(
                    ModeActivity.this,
                    WifiActivity.class
            );

            startActivity(i);
        });
    }
}
