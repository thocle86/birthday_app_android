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
import android.widget.EditText;

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

    private ActivityLoginBinding binding;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private User mUser;

    public Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mHandler = new Handler();

        mUsernameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login);
        mProgressView = findViewById(R.id.loading);

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
                String username = mUsernameView.getText().toString();
                String password = mPasswordView.getText().toString();

                mLoginFormView.setEnabled(Util.isUserNameValid(username) && Util.isPasswordValid(password));
            }
        };

        mUsernameView.addTextChangedListener(textWatcher);
        mPasswordView.addTextChangedListener(textWatcher);

        mPasswordView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // TODO : appeler la méthode pour tenter le login
                this.attemptLogin();
            }
            return false;
        });

        mLoginFormView.setOnClickListener(v -> {
            // TODO : appeler la méthode pour tenter le login
            this.attemptLogin();
        });
    }

    private void attemptLogin() {

        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString().trim();
        Log.d("LOG", username);
        String password = mPasswordView.getText().toString().trim();
        Log.d("LOG", password);

        boolean cancel = false;
        View focusView = null;

        if (!Util.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!Util.isUserNameValid(username)) {
            mUsernameView.setError(getString(R.string.invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", username);
            credentials.put("password", password);

            // TODO : Appeler la méthode permettant de faire un appel API via POST
            Log.d("LOG", UtilApi.URL_LOGIN);
            UtilApi.post(UtilApi.URL_LOGIN, credentials, null,this);
        }
    }

    private void showProgress(boolean visible) {
        mProgressView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onFailure(String json) {
        mProgressView.setVisibility(View.INVISIBLE);
        mHandler.post(() -> {
            Log.d("LOG", "!!! ON FAILURE !!!: ");
            Log.d("LOG", "fail_json: " + json);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Une erreur est survenue !")
                    .setMessage("Login ou Mot de passe invalide.\nVeuillez réessayer.")
                    .setPositiveButton("OK", (dialog, which) -> {})
                    .create()
                    .show();
            mUsernameView.setText("");
            mPasswordView.setText("");
        });
    }

    @Override
    public void onResponseSuccess(String json) {
        mHandler.post(() -> {
            Log.d("LOG", "*** ON RESPONSE SUCCESS ***");
            Log.d("LOG", "success_json: " + json);
            // TODO : Etablisser un comportement lors d'un success
            try {
                Util.setUser(this, json);
                mUser = Util.getUser(this);
                Log.d("LOG", "token: " + mUser.token);
                Log.d("LOG", "username: " + mUser.username);
                // TODO : Faites la redirection
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } catch (Exception e) {
                Log.d("LOG", "Problemos !!!!!");
            }

        });
    }

    @Override
    public void onResponseFail(String json) {
        mProgressView.setVisibility(View.INVISIBLE);
        mHandler.post(() -> {
            Log.d("LOG", "!!! ON RESPONSE FAIL !!!: ");
            Log.d("LOG", "fail_json: " + json);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Une erreur est survenue !")
                    .setMessage("Login ou Mot de passe invalide.\nVeuillez réessayer.")
                    .setPositiveButton("OK", (dialog, which) -> {})
                    .create()
                    .show();
            mUsernameView.setText("");
            mPasswordView.setText("");
        });
    }
}