package fr.cefim.android.birthday_app.utils;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.cefim.android.birthday_app.adapters.ListItem;
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
        return new User(context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).getString(USER, ""));
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

        int monthNumber = 0;
        String[] months = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre"};

        // TODO : trier la liste en fonction des mois d'anniversaire

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
