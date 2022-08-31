package fr.cefim.android.birthday_app.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import fr.cefim.android.birthday_app.utils.Util;

public class Birthday {

    public Long id;
    public Date date;
    public String firstname;
    public String lastname;

    //    {
    //            "date": "1988-02-02",
    //            "firstname": "Peter",
    //            "lastname": "Bardu"
    //        }
    public Birthday(String json) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject(json);

        id = jsonObject.getLong("id");
        date = Util.initDateFromDB(jsonObject.getString("date"));
        firstname = jsonObject.getString("firstname");
        lastname = jsonObject.getString("lastname");
    }

    public Birthday(Long id, Date date, String firstname, String lastname) {
        this.id = id;
        this.date = date;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("date", Util.printDate(date));
            json.put("firstname", firstname);
            json.put("lastname", lastname);
        } catch (JSONException e) {
        }
        return json;
    }


    public static class CustomComparator implements Comparator<Birthday> {
        @Override
        public int compare(Birthday birthday1, Birthday birthday2) {

            if (birthday1.date.getMonth() > birthday2.date.getMonth())
                return 1;
            else if (birthday1.date.getMonth() == birthday2.date.getMonth()) {
                if (birthday1.date.getDate() > birthday2.date.getDate())
                    return 1;
                else if (birthday1.date.getDate() == birthday2.date.getDate())
                    return 0;
                else return -1;
            } else
                return -1;
        }
    }
}
