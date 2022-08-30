package fr.cefim.android.birthday_app.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import fr.cefim.android.birthday_app.adapters.BirthdayItem;
import fr.cefim.android.birthday_app.adapters.ListItem;
import fr.cefim.android.birthday_app.adapters.MonthItem;
import fr.cefim.android.birthday_app.models.Birthday;
import fr.cefim.android.birthday_app.models.User;

public class Util {

    private static final String PREF_FILE = "pref_file";
    private static final String USER = "user";

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat FORMAT_INPUT = new SimpleDateFormat("dd/MM/yyyy");

    public static void setUser(Context context, String json) {
        context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit().putString(USER, json).apply();
    }

    public static User getUser(Context context) throws JSONException, ParseException {
        String json = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).getString(USER, "");
        return new User(json);
    }

    public static Date initDateFromDB(String str) throws ParseException {
        return FORMAT.parse(str);
    }

    public static String printDate(Date date) {
        return FORMAT.format(date);
    }

    public static long getAge(Date date) {
        long diff = System.currentTimeMillis() - date.getTime();
        return diff / 31622400000l;
    }

    public static boolean isUserNameValid(String userName) {
        if (userName == null || TextUtils.isEmpty(userName)) {
            return false;
        }
        return userName.trim().length() > 3;
    }

    public static boolean isPasswordValid(String password) {
        if (password == null || TextUtils.isEmpty(password)) {
            return false;
        }
        return password.trim().length() > 3;
    }

    public static ArrayList<ListItem> createListItems(ArrayList<Birthday> birthdays) {

        ArrayList<ListItem> listItems = new ArrayList<>();

        int monthNumber = 100;
        String[] months = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre"};

        for (String month : months) {
            MonthItem monthItem = new MonthItem(month, monthNumber);
            listItems.add(monthItem);
            monthNumber += 100;
        }

        for (Birthday birthday : birthdays) {
            BirthdayItem birthdayItem = new BirthdayItem(birthday);
            listItems.add(birthdayItem);
        }

        // TODO : trier la liste en fonction des mois d'anniversaire
        Collections.sort(listItems);

        return listItems;
    }

    public static boolean isDateValid(String string) {
        try {
            FORMAT_INPUT.parse(string);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static Date initDateFromEditText(String str) throws ParseException {
        return FORMAT_INPUT.parse(str);
    }
}
