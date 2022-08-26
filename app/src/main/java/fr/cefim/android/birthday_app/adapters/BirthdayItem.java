package fr.cefim.android.birthday_app.adapters;

import fr.cefim.android.birthday_app.models.Birthday;

public class BirthdayItem extends ListItem {

    public Birthday birthday;

    public BirthdayItem(Birthday birthday) {
        this.birthday = birthday;
    }

    @Override
    public int getType() {
        return TYPE_BIRTHDAY;
    }
}
