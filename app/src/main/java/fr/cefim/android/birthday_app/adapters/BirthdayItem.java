package fr.cefim.android.birthday_app.adapters;

import fr.cefim.android.birthday_app.models.Birthday;

public class BirthdayItem extends ListItem {

    public Birthday mBirthday;

    public BirthdayItem(Birthday birthday) {
        mBirthday = birthday;
        this.index = (mBirthday.date.getMonth() + 1) * 100 + mBirthday.date.getDate();
    }

    @Override
    public int getType() {
        return TYPE_BIRTHDAY;
    }
}
