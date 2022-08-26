package fr.cefim.android.birthday_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.text.ParseException;

import fr.cefim.android.birthday_app.models.User;
import fr.cefim.android.birthday_app.utils.Util;

public class SplashScreenActivity extends AppCompatActivity {

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        try {
//            mUser = Util.getUser(this);
//            Log.d("LOG", "user splash activity: " + mUser.username);
//            startActivity(new Intent(this, MainActivity.class));
//        } catch (Exception e) {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        }

        startActivity(new Intent(this, LoginActivity.class));
        finish();

    }
}