package fr.cefim.android.birthday_app.adapters;

public abstract class ListItem implements Comparable<ListItem> {

    public static final int TYPE_MONTH = 0;

    public static final int TYPE_BIRTHDAY = 1;

    abstract public int getType();

    public int index;

    /**
     * Un index est donné à chaque ListItem en suivant la règle suivante: janvier = 100, février = 200, etc ..., décembre = 1200
     * Les dates reçoivent un index sur la même logique: 12 janvier => 112, 25 juin => 625, etc..
     */
    @Override
    public int compareTo(ListItem listItem) {
        return this.index - listItem.index;
    }

}
