package com.ahmed.guessduel;package com.ahmed android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.*;

public class GameActivity extends Activity {

    int secret, lives = 5;
    int setter, guesser;
    int scoreP1, scoreP2;

    String name1, name2;

    final int WIN_SCORE = 5;

    MediaPlayer clickSound, winSound, loseSound;

    EditText input;
    TextView result, scoreText, turnText, livesText;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // UI
        input = findViewById(R.id.input);
        result = findViewById(R.id.resultText);
        scoreText = findViewById(R.id.scoreText);
        turnText = findViewById(R.id.turnText);
        livesText = findViewById(R.id.livesText);
        btn = findViewById(R.id.submitBtn);

        // Sounds
        clickSound = MediaPlayer.create(this, R.raw.click);
        winSound  = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);

        // Data
        secret = getIntent().getIntExtra("secret", 0);
        setter = getIntent().getIntExtra("setter", 1);
        scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        scoreP2 = getIntent().getIntExtra("scoreP2", 0);
        name1 = getIntent().getStringExtra("name1");
        name2 = getIntent().getStringExtra("name2");

        if (name1 == null) name1 = "Player 1";
        if (name2 == null) name2 = "Player 2";

        guesser = (setter == 1) ? 2 : 1;

        startRound();

        btn.setOnClickListener(v -> handleGuess());
    }

    // ✅ Start round
    private void startRound() {
        lives = 5;
        input.setText("");
        result.setText("");

        turnText.setText(getName(guesser) + " guessing...");
        updateUI();
    }

    // ✅ Get player name
    private String getName(int p) {
        return (p == 1) ? name1 : name2;
    }

    // ✅ Update UI
    private void updateUI() {


