package com.ahmed.guessduel;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.*;

public class GameActivity extends Activity {

    int secret, lives = 5, setter, guesser;
    int scoreP1, scoreP2;
    String name1, name2;

    MediaPlayer clickSound, winSound, loseSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        EditText input = findViewById(R.id.input);
        TextView result = findViewById(R.id.resultText);
        TextView scoreText = findViewById(R.id.scoreText);
        TextView turnText = findViewById(R.id.turnText);
        TextView livesText = findViewById(R.id.livesText);
        Button btn = findViewById(R.id.submitBtn);

        clickSound = MediaPlayer.create(this, R.raw.click);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);

        secret = getIntent().getIntExtra("secret", 0);
        setter = getIntent().getIntExtra("setter", 1);
        name1 = getIntent().getStringExtra("name1");
        name2 = getIntent().getStringExtra("name2");
        scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        scoreP2 = getIntent().getIntExtra("scoreP2", 0);

        guesser = setter == 1 ? 2 : 1;

        turnText.setText(getName(guesser) + " guessing...");
        updateUI(scoreText, livesText);

        btn.setOnClickListener(v -> {

            if (clickSound != null) clickSound.start();

            String text = input.getText().toString();
            if (text.isEmpty()) return;

            int g = Integer.parseInt(text);

            if (g < secret) {
                result.setText("Higher");
                lives--;
            } else if (g > secret) {
                result.setText("Lower");
                lives--;
            } else {

                if (winSound != null) winSound.start();

                if (guesser == 1) scoreP1++;
                else scoreP2++;

                nextTurn(result);
                return;
            }

            updateUI(scoreText, livesText);

            if (lives == 0) {
                if (loseSound != null) loseSound.start();

                if (setter == 1) scoreP1++;
                else scoreP2++;

                nextTurn(result);
            }
        });
    }

    private String getName(int p) {
        return p == 1 ? name1 : name2;
    }

    private void updateUI(TextView score, TextView livesText) {
        score.setText(name1 + ": " + scoreP1 + " | " + name2 + ": " + scoreP2);
        livesText.setText("Lives: " + lives);
    }

    private void nextTurn(TextView result) {

        int newSetter = guesser;

        result.postDelayed(() -> {
            Intent i = new Intent(this, SetupActivity.class);
            i.putExtra("setter", newSetter);
            i.putExtra("name1", name1);
            i.putExtra("name2", name2);
            i.putExtra("scoreP1", scoreP1);
            i.putExtra("scoreP2", scoreP2);

            startActivity(i);
            finish();

        }, 1000);
    }
}
