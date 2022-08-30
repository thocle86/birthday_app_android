package fr.cefim.android.birthday_app.models;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import fr.cefim.android.birthday_app.utils.Util;

public class User {

    public String mStringJson;
    public Long id;
    public String username;
    public String token;
    public String email;
    public ArrayList<Birthday> birthdays;

    //{"id":1,"username":"peter","email":"peter.bardu@gmail.com", "birthdays": [
    //        {
    //            "date": "1988-02-02",
    //            "firstName": "Peter",
    //            "lastName": "Bardu"
    //        }
    //    ]
    // }
    public User(String jsonResponse) throws JSONException, ParseException {
        mStringJson = jsonResponse;
        JSONObject jsonObject = new JSONObject(mStringJson);
        JSONObject jsonMyPrincipalUser = jsonObject.getJSONObject("myPrincipalUser");
        JSONObject jsonUser = jsonMyPrincipalUser.getJSONObject("user");
        id = jsonUser.getLong("id");
        token = "Bearer " + jsonObject.getString("token");
        username = jsonUser.getString("username");
        email = jsonUser.getString("email");
        birthdays = new ArrayList<>();

        JSONArray jsonArray = jsonUser.getJSONArray("birthdays");
        for (int i = 0; i < jsonArray.length(); i++) {
            birthdays.add(new Birthday(jsonUser.getJSONArray("birthdays").getJSONObject(i).toString()));
        }
    }

    public void addBirthday(Context context, Birthday birthday) {
        birthdays.add(birthday);

        try {
            JSONObject jsonObject = new JSONObject(mStringJson);
            JSONObject jsonUser = jsonObject.getJSONObject("user");
            jsonUser.getJSONArray("birthdays").put(birthday.toJson());
            mStringJson = jsonUser.toString();

            Log.d("lol", "addBirthday: " + mStringJson);

            Util.setUser(context, mStringJson);
        } catch (JSONException e) {
            // TODO
        }
    }

}
