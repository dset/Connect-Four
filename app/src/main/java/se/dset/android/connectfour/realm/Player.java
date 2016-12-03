package se.dset.android.connectfour.realm;

import io.realm.RealmObject;

public class Player extends RealmObject {
    private String name;

    public Player() {}

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
