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
        String name1 = getIntent().getStringExtra("name1");
        String name2 = getIntent().getStringExtra("name2");

        int scoreP1 = getIntent().getIntExtra("scoreP1", 0);
        int scoreP2 = getIntent().getIntExtra("scoreP2", 0);

        String setterName = setter == 1 ? name1 : name2;
        title.setText(setterName + " sets number");

        btn.setOnClickListener(v -> {

            String text = input.getText().toString();  // ✅ ADD THIS
        
            if (text.isEmpty()) return;
        
            int secret = Integer.parseInt(text);
        
            if (secret < 0 || secret > 100) {
                input.setError("0 - 100 only");
                return;
            }

            Intent i = new Intent(this, GameActivity.class);
            i.putExtra("secret", num);
            i.putExtra("setter", setter);
            i.putExtra("name1", name1);
            i.putExtra("name2", name2);
            i.putExtra("scoreP1", scoreP1);
            i.putExtra("scoreP2", scoreP2);

            startActivity(i);
        });        
    }
}
