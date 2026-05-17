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

        Button pvp = findViewById(R.id.pvpBtn);
        Button ai = findViewById(R.id.aiBtn);

        pvp.setOnClickListener(v -> openPlayers(false));
        ai.setOnClickListener(v -> openPlayers(true));
    }

    private void openPlayers(boolean isAI) {
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("isAI", isAI);
        startActivity(i);
    }
}
