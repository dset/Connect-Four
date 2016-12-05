package se.dset.android.connectfour.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/* Represents a player. Players are identified by their names. */
public class Player extends RealmObject {
    @PrimaryKey
    @Required
    private String name;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
