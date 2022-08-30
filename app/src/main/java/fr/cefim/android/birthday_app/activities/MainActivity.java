package fr.cefim.android.birthday_app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.cefim.android.birthday_app.R;
import fr.cefim.android.birthday_app.adapters.BirthdayAdapter;
import fr.cefim.android.birthday_app.adapters.BirthdayItem;
import fr.cefim.android.birthday_app.adapters.ListItem;
import fr.cefim.android.birthday_app.databinding.ActivityMainBinding;
import fr.cefim.android.birthday_app.models.Birthday;
import fr.cefim.android.birthday_app.models.User;
import fr.cefim.android.birthday_app.utils.ApiCallback;
import fr.cefim.android.birthday_app.utils.Util;
import fr.cefim.android.birthday_app.utils.UtilApi;

public class MainActivity extends AppCompatActivity implements ApiCallback {

    private ActivityMainBinding mBinding;
    private BirthdayAdapter mBirthdayAdapter;
    private User mUser;
    public Handler handler;
    private List<ListItem> mListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        handler = new Handler();

//        Toolbar toolbar = findViewById(R.id.toolbar);
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);

        try {
            mUser = Util.getUser(this);
            Log.d("LOG", "user main activity: " + mUser.username);
        } catch (Exception e) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        mListItems = Util.createListItems(mUser.birthdays);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view_home);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mBirthdayAdapter = new BirthdayAdapter(this, mListItems);
        recyclerView.setAdapter(mBirthdayAdapter);

        findViewById(R.id.fab).setOnClickListener(v -> showDialogAddNewBirthday());
    }

    private void showDialogAddNewBirthday() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_add_new_birthdate, null);
        final EditText editTextFirstName = view.findViewById(R.id.edit_text_text_first_name);
        final EditText editTextLastName = view.findViewById(R.id.edit_text_text_last_name);
        final EditText editTextDate = view.findViewById(R.id.edit_text_text_date);

        editTextDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Util.isDateValid(s.toString())) {
                    editTextDate.setError("Date incorrecte");
                }
            }
        });

        builder.setTitle("Nouvel anniversaire ?");
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            // TODO : récupérer les valeurs et appeler la méthode addNewBirthday

            String date = editTextDate.getText().toString();
            String firstname = editTextFirstName.getText().toString();
            String lastname = editTextLastName.getText().toString();
            this.addNewBirthday(date, firstname, lastname);
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void addNewBirthday(String dateStr, String firstname, String lastname) {
        try {
            if (dateStr == null || dateStr.isEmpty()) {
                throw new Exception("Date incorrecte");
            }

            Date date = Util.initDateFromEditText(dateStr);

            if (firstname == null || firstname.isEmpty()) {
                throw new Exception("Prénom incorrecte");
            }

            if (lastname == null || lastname.isEmpty()) {
                throw new Exception("Nom incorrecte");
            }

//            Birthday birthday = new Birthday(date, firstname, lastname);

            // TODO : Appeler la méthode qui ajoute cet anniversaire à la liste des anniversaires de cet utilisateur (comprendre ce que fait la méthode)

            // Appel API POST /users/id/birthdays
            Map<String, String> map = new HashMap<>();
            map.put("date", Util.printDate(date));
            map.put("firstname", firstname);
            map.put("lastname", lastname);

            String url = String.format(UtilApi.CREATE_BIRTHDAY, mUser.id);

            UtilApi.post(url, map, mUser.token, this);

        } catch (ParseException e) {
            Toast.makeText(MainActivity.this, "Date incorrecte", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(String json) {
        handler.post(() -> {
            Log.d("LOG", "!!! ON FAILURE !!!: ");
            Log.d("LOG", "fail_json: " + json);
        });
    }

    @Override
    public void onResponseSuccess(String json) {
        handler.post(() -> {
            Log.d("LOG", "*** ON RESPONSE SUCCESS ***");
            Log.d("LOG", "success_json: " + json);
            Snackbar.make(findViewById(R.id.coordinator_root), "Anniversaire ajouté", Snackbar.LENGTH_SHORT).show();

            try {
                Birthday birthday = new Birthday(json);
                mUser.addBirthday(this, birthday);
                BirthdayItem birthdayItem = new BirthdayItem(birthday);
                Log.d("LOG", String.valueOf(birthdayItem.index));
                mListItems.add(birthdayItem);
                Collections.sort(mListItems);
                mBirthdayAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void onResponseFail(String json) {
        handler.post(() -> {
            Log.d("LOG", "!!! ON RESPONSE FAIL !!!: ");
            Log.d("LOG", "fail_json: " + json);
        });
    }

}