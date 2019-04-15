package com.leemartinc.zodiacadventures;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class loader extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// decide here whether to navigate to Login or Main Activity

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if (pref.getBoolean("activity_executed", false)) {
            Intent intent = new Intent(this, resume.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, create.class);
            startActivity(intent);
            finish();
        }
    }

}
