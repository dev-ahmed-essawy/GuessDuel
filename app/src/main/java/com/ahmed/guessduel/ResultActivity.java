package com.ahmed.guessduel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView result = findViewById(R.id.resultText);
        Button restart = findViewById(R.id.restartBtn);
        ImageView trophy = findViewById(R.id.trophy);

        String winner = getIntent().getStringExtra("winner");

        result.setText(winner + " WINS!");

        trophy.animate().scaleX(1.3f).scaleY(1.3f).setDuration(500)
                .withEndAction(() -> trophy.animate().scaleX(1f).scaleY(1f).setDuration(500));

        restart.setOnClickListener(v -> finishAffinity());
    }
}
