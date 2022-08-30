package fr.cefim.android.birthday_app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import fr.cefim.android.birthday_app.R;
import fr.cefim.android.birthday_app.databinding.ActivityLoginBinding;
import fr.cefim.android.birthday_app.models.User;
import fr.cefim.android.birthday_app.utils.ApiCallback;
import fr.cefim.android.birthday_app.utils.Util;
import fr.cefim.android.birthday_app.utils.UtilApi;

public class LoginActivity extends AppCompatActivity implements ApiCallback {

    private ActivityLoginBinding b;

    private User mUser;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        mHandler = new Handler();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = b.loginEditTextUsername.getText().toString();
                String password = b.loginEditTextPassword.getText().toString();

                b.loginButtonSignInOrSignUp.setEnabled(Util.isUserNameValid(username) && Util.isPasswordValid(password));
            }
        };

        b.loginEditTextUsername.addTextChangedListener(textWatcher);
        b.loginEditTextPassword.addTextChangedListener(textWatcher);

        b.loginEditTextPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                this.attemptLogin();
            }
            return false;
        });

        b.loginButtonSignInOrSignUp.setOnClickListener(v -> {
            this.attemptLogin();
        });
    }

    private void attemptLogin() {
        b.loginEditTextUsername.setError(null);
        b.loginEditTextPassword.setError(null);
        String username = b.loginEditTextUsername.getText().toString().trim();
        String password = b.loginEditTextPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (!Util.isPasswordValid(password)) {
            b.loginEditTextPassword.setError(getString(R.string.invalid_password));
            focusView = b.loginEditTextPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            b.loginEditTextUsername.setError(getString(R.string.error_field_required));
            focusView = b.loginEditTextUsername;
            cancel = true;
        } else if (!Util.isUserNameValid(username)) {
            b.loginEditTextUsername.setError(getString(R.string.invalid_username));
            focusView = b.loginEditTextUsername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", username);
            credentials.put("password", password);

            UtilApi.post(UtilApi.URL_LOGIN, credentials, null, this);
        }
    }

    private void showProgress(boolean visible) {
        b.loginProgressBarLoader.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onFailure(String json) {
        b.loginProgressBarLoader.setVisibility(View.INVISIBLE);
        mHandler.post(() -> {
            Log.d("LOG", "!!! ON FAILURE !!!: ");
            Log.d("LOG", "fail_json: " + json);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Une erreur est survenue !")
                    .setMessage("Login ou Mot de passe invalide.\nVeuillez réessayer.")
                    .setPositiveButton("OK", (dialog, which) -> {
                    })
                    .create()
                    .show();
            b.loginEditTextUsername.setText("");
            b.loginEditTextPassword.setText("");
        });
    }

    @Override
    public void onResponseSuccess(String json) {
        mHandler.post(() -> {
            Log.d("LOG", "*** ON RESPONSE SUCCESS ***");
            Log.d("LOG", "success_json: " + json);
            try {
                Util.setUser(this, json);
                mUser = Util.getUser(this);
                Log.d("LOG", "token: " + mUser.token);
                Log.d("LOG", "username: " + mUser.username);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } catch (Exception e) {
                Log.d("LOG", "Problemos !!!!!");
            }

        });
    }

    @Override
    public void onResponseFail(String json) {
        b.loginProgressBarLoader.setVisibility(View.INVISIBLE);
        mHandler.post(() -> {
            Log.d("LOG", "!!! ON RESPONSE FAIL !!!: ");
            Log.d("LOG", "fail_json: " + json);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Une erreur est survenue !")
                    .setMessage("Login ou Mot de passe invalide.\nVeuillez réessayer.")
                    .setPositiveButton("OK", (dialog, which) -> {
                    })
                    .create()
                    .show();
            b.loginEditTextUsername.setText("");
            b.loginEditTextPassword.setText("");
        });
    }
}