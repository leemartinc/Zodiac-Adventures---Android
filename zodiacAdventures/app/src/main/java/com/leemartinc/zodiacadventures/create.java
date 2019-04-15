package com.leemartinc.zodiacadventures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leemartinc.zodiacadventures.R;

import java.util.Random;



public class create extends AppCompatActivity {

    //SharedPreferences prefs;
    SharedPreferences.Editor userInfo;
    TextView name;
    private String gameClass;
    private int player_def;
    private int player_skill1;
    double mod;
    Intent intent;
    private int prob;
    private int player_skill2;
    private int player_health = 0;
    RadioGroup radioGroup;
    RadioGroup radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_char);

        Random rand = new Random();
        //randomNum = minimum + rand.nextInt((maximum - minimum) + 1);
        prob = 6 + rand.nextInt((9-6)+1);


        intent = new Intent(this, MainActivity.class);
        userInfo = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE).edit();
        radioGroup = findViewById(R.id.gameClass);

       name = findViewById(R.id.gameName);

        //onRadioButtonClicked();
        //checkButton();

        Button start =findViewById(R.id.create);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                if (player_health != 0 && name.getText().toString().length() > 1){
                    userInfo.putString("CHAR_NAME", name.getText().toString());
                    //userInfo.putString("CHAR_ICON", name.getText().toString().charAt(0));
                    userInfo.putInt("DEFENSE", player_def);
                    userInfo.putInt("SKILL1", player_skill1);
                    userInfo.putInt("SKILL2", player_skill2);
                    userInfo.putInt("HEALTH", player_health);
                    userInfo.putLong("POWER", Double.doubleToRawLongBits(0));
                    userInfo.putString("CLASS", gameClass);
                    userInfo.apply();


                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Fill in all data!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void onRadioButtonClicked(View v) {
        RadioButton warrior = (RadioButton) findViewById(R.id.warrior);
        RadioButton ninja = (RadioButton) findViewById(R.id.ninja);
        RadioButton knight = (RadioButton) findViewById(R.id.knight);
        RadioButton priest = (RadioButton) findViewById(R.id.priest);
        RadioButton god = (RadioButton) findViewById(R.id.godMode);

        // Is the button now checked?
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.warrior:
                if (checked)
                    // warrior are the best
                    player_def = 4;
                    player_skill1 = 20;
                    mod = prob/10.0;
                    player_skill2 = (int)Math.round((player_skill1 + player_skill1) * (mod));
                    player_health = 100;
                    gameClass = "warrior";
                break;
            case R.id.ninja:
                if (checked)
                    // Ninjas rule
                    player_def = 10;
                    player_skill1 = 15;
                    mod = prob/10.0;
                    player_skill2 = (int)Math.round((player_skill1 + player_skill1) * (mod));
                    player_health = 70;
                    gameClass = "ninja";
                break;
            case R.id.priest:
                if (checked)
                    // Preists are amazinggggg
                    player_def = 4;
                    player_skill1 = 25;
                    mod = prob/10.0;
                    player_skill2 = (int)Math.round((player_skill1 + player_skill1) * (mod));
                    player_health = 65;
                    gameClass = "priest";
                break;
            case R.id.knight:
                if (checked)
                    // knights fight
                    player_def = 7;
                    player_skill1 = 30;
                    mod = prob/10.0;
                    player_skill2 = (int)Math.round((player_skill1 + player_skill1) * (mod));
                    player_health = 85;
                    gameClass = "knight";
                break;
            case R.id.godMode:
                if (checked)
                    // its over 900000000000000000000
                    player_def = 99999;
                    player_skill1 = 99999999;
                    mod = prob/10.0;
                    player_skill2 = (int)Math.round((player_skill1 + player_skill1) * (mod));
                    player_health = 99999999;
                    gameClass = "god";
                break;
        }
    }
}
