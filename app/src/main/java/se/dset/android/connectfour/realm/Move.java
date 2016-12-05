package se.dset.android.connectfour.realm;

import io.realm.RealmObject;

public class Move extends RealmObject {
    private Player player;
    private int row;
    private int column;
    private long timestamp;

    public Move() {
    }

    public Move(Player player, int row, int column) {
        this.player = player;
        this.row = row;
        this.column = column;
        this.timestamp = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
