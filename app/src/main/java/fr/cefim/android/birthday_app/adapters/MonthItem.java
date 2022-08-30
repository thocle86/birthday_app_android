package fr.cefim.android.birthday_app.adapters;

public class MonthItem extends ListItem {

    public String month;

    public MonthItem(String month, int number) {
        this.month = month;
        this.index = number;
    }

    @Override
    public int getType() {
        return TYPE_MONTH;
    }

}
