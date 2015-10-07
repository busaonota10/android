package br.com.busaonota10.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import br.com.busaonota10.R;


public class MainActivity extends Activity {
    private static int SPLASH_TIME_OUT = 1000;
    private Intent intent;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (sharedPref.getBoolean("firstTime", true)) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("firstTime", false);
                    editor.apply();

                    intent = new Intent(MainActivity.this, SplashScreenActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, FeedbackActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
