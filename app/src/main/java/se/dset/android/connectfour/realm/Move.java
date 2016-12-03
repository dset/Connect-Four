package se.dset.android.connectfour.realm;

import io.realm.RealmObject;

public class Move extends RealmObject {
    private Player player;
    private int row;
    private int column;

    public Move() {}

    public Move(Player player, int row, int column) {
        this.player = player;
        this.row = row;
        this.column = column;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}