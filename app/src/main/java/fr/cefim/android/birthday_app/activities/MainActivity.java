package fr.cefim.android.birthday_app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.cefim.android.birthday_app.R;
import fr.cefim.android.birthday_app.adapters.BirthdayAdapter;
import fr.cefim.android.birthday_app.adapters.BirthdayItem;
import fr.cefim.android.birthday_app.adapters.ListItem;
import fr.cefim.android.birthday_app.databinding.ActivityMainBinding;
import fr.cefim.android.birthday_app.databinding.DialogAddNewBirthdayBinding;
import fr.cefim.android.birthday_app.models.Birthday;
import fr.cefim.android.birthday_app.models.User;
import fr.cefim.android.birthday_app.utils.ApiCallback;
import fr.cefim.android.birthday_app.utils.Util;
import fr.cefim.android.birthday_app.utils.UtilApi;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;

    private BirthdayAdapter mBirthdayAdapter;

    private User mUser;

    private Handler mHandler;

    private List<ListItem> mListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        mHandler = new Handler();

        Toolbar toolbar = b.toolbar;
        setSupportActionBar(toolbar);

        try {
            mUser = Util.getUser(this);
            Log.d("LOG", "user main activity: " + mUser.username);
        } catch (Exception e) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        mListItems = Util.createListItems(mUser.birthdays);
        mBirthdayAdapter = new BirthdayAdapter(this, mListItems);
        b.contentList.recyclerViewBirthdays.setLayoutManager(new LinearLayoutManager(this));
        b.contentList.recyclerViewBirthdays.setAdapter(mBirthdayAdapter);

        b.floatingActionButtonAddBirthday.setOnClickListener(v -> showDialogAddNewBirthday());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * To remove swipe in Month item
             */
            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof BirthdayAdapter.MonthViewHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                BirthdayItem birthdayItem = (BirthdayItem) mListItems.get(viewHolder.getAdapterPosition());
                AtomicBoolean isBirthdayDeleted = new AtomicBoolean(true);
                int position = viewHolder.getAdapterPosition();
                mListItems.remove(position);
                mBirthdayAdapter.notifyItemRemoved(position);

                Snackbar
                        .make(
                                b.mainCoordinatorLayout,
                                String.format("%s %s est supprimé", birthdayItem.birthday.firstname, birthdayItem.birthday.lastname),
                                Snackbar.LENGTH_LONG
                        )
                        .setAction("Annuler", v -> {
                            mListItems.add(position, birthdayItem);
                            mBirthdayAdapter.notifyItemInserted(position);
                            isBirthdayDeleted.set(false);
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT && isBirthdayDeleted.get()) {
                                    deleteBirthday(birthdayItem.birthday);
                                }
                            }
                        })
                        .show();
            }
        });
        itemTouchHelper.attachToRecyclerView(b.contentList.recyclerViewBirthdays);
    }

    private void showDialogAddNewBirthday() {
        DialogAddNewBirthdayBinding b = DialogAddNewBirthdayBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(b.getRoot());

        b.editTextDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Util.isDateValid(s.toString())) {
                    b.editTextDate.setError("Date incorrecte");
                }
            }
        });

        builder.setTitle("Nouvel anniversaire ?");
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            String date = b.editTextDate.getText().toString();
            String firstname = b.editTextFirstname.getText().toString();
            String lastname = b.editTextLastname.getText().toString();
            this.addBirthday(date, firstname, lastname);
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void addBirthday(String dateStr, String firstname, String lastname) {
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

            Map<String, String> birthdayBody = new HashMap<>();
            birthdayBody.put("date", Util.printDate(date));
            birthdayBody.put("firstname", firstname);
            birthdayBody.put("lastname", lastname);

            String url = String.format(UtilApi.CREATE_BIRTHDAY, mUser.id);

            UtilApi.post(url, birthdayBody, mUser.token, new ApiCallback() {
                @Override
                public void onFailure(String json) {
                    mHandler.post(() -> {
                        Log.d("LOG", "!!! ON FAILURE !!!: ");
                        Log.d("LOG", "fail_json: " + json);
                    });
                }

                @Override
                public void onResponseSuccess(String json) {
                    mHandler.post(() -> {
                        Log.d("LOG", "*** ON RESPONSE SUCCESS ***");
                        Log.d("LOG", "success_json: " + json);
                        Snackbar.make(findViewById(R.id.main_coordinator_layout), "Anniversaire ajouté", Snackbar.LENGTH_SHORT).show();

                        try {
                            Birthday birthday = new Birthday(json);
                            mUser.addBirthday(MainActivity.this, birthday);
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
                    mHandler.post(() -> {
                        Log.d("LOG", "!!! ON RESPONSE FAIL !!!: ");
                        Log.d("LOG", "fail_json: " + json);
                    });
                }
            });

        } catch (ParseException e) {
            Toast.makeText(MainActivity.this, "Date incorrecte", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBirthday(Birthday birthday) {
        Log.d("LOG", "birthday id: " + birthday.id);
        UtilApi.delete(String.format(UtilApi.DELETE_BIRTHDAY, mUser.id, birthday.id), mUser.token, new ApiCallback() {
            @Override
            public void onFailure(String json) {
                mHandler.post(() -> {
                    Log.d("LOG", "!!! ON FAILURE !!!: ");
                    Log.d("LOG", "fail_json: " + json);
                });
            }

            @Override
            public void onResponseSuccess(String json) {
                mHandler.post(() -> {
                    Log.d("LOG", "*** ON RESPONSE SUCCESS ***");
                    Log.d("LOG", "success_json: " + json);
                });
            }

            @Override
            public void onResponseFail(String json) {
                mHandler.post(() -> {
                    Log.d("LOG", "!!! ON RESPONSE FAIL !!!: ");
                    Log.d("LOG", "fail_json: " + json);
                });
            }
        });

    }

}