package fr.cefim.android.birthday_app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (login ok){
//            startActivity(new Intent(this, MainActivity.class));
//        } else{
//            startActivity(new Intent(this, LoginActivity.class));
//        }
        startActivity(new Intent(this, LoginActivity.class));
        finish();

    }
}