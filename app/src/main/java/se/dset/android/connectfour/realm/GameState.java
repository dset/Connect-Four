package se.dset.android.connectfour.realm;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class GameState extends RealmObject {
    @PrimaryKey
    @Required
    private String id;

    private int rows;
    private int columns;
    private int winningCondition;
    private RealmList<Player> players;
    private RealmList<Move> moves;
    private long timestamp;

    public GameState() {
    }

    public GameState(String id, int rows, int columns, int winningCondition, RealmList<Player> players) {
        this.id = id;
        this.rows = rows;
        this.columns = columns;
        this.winningCondition = winningCondition;
        this.players = players;
        this.moves = new RealmList<>();
        this.timestamp = System.currentTimeMillis();
    }

    public Move placeInColumn(int column) {
        if (isGameOver()) {
            return null;
        }

        int row = 0;
        for (Move move : getMoves()) {
            if (move.getColumn() == column) {
                row++;
            }
        }

        if (row >= rows) {
            return null;
        }

        Move move = new Move(getCurrentPlayer(), row, column);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        getMoves().add(move);
        realm.commitTransaction();
        realm.close();

        return move;
    }

    public Player getCurrentPlayer() {
        int index = getMoves().size() % getPlayers().size();
        return getPlayers().get(index);
    }

    public boolean isGameOver() {
        return hasGameWinner() || getMoves().size() >= (rows * columns);
    }

    public boolean hasGameWinner() {
        if (getMoves().isEmpty()) {
            return false;
        }

        String[][] board = new String[rows][columns];
        for (Move move : getMoves()) {
            board[move.getRow()][move.getColumn()] = move.getPlayer().getName();
        }

        Move lastMove = getMoves().last();
        int lastMoveRow = lastMove.getRow();
        int lastMoveColumn = lastMove.getColumn();

        int connectedVertical = 1 + numConnectedInDirection(board, lastMoveRow, lastMoveColumn, -1, 0);
        int connectedHorizontal = 1 + numConnectedInDirection(board, lastMoveRow, lastMoveColumn, 0, -1)
                + numConnectedInDirection(board, lastMoveRow, lastMoveColumn, 0, 1);
        int connectedDiagonal1 = 1 + numConnectedInDirection(board, lastMoveRow, lastMoveColumn, -1, -1)
                + numConnectedInDirection(board, lastMoveRow, lastMoveColumn, 1, 1);
        int connectedDiagonal2 = 1 + numConnectedInDirection(board, lastMoveRow, lastMoveColumn, 1, -1)
                + numConnectedInDirection(board, lastMoveRow, lastMoveColumn, -1, 1);

        return connectedVertical >= winningCondition || connectedHorizontal >= winningCondition
                || connectedDiagonal1 >= winningCondition || connectedDiagonal2 >= winningCondition;
    }

    private int numConnectedInDirection(String[][] board, int startRow, int startColumn, int dR, int dC) {
        String name = board[startRow][startColumn];
        int connected = 0;
        for (int row = startRow + dR, column = startColumn + dC;
             row >= 0 && row < board.length && column >= 0 && column < board[0].length;
             row += dR, column += dC) {
            if (!name.equals(board[row][column])) {
                break;
            }

            connected++;
        }

        return connected;
    }

    public Player getWinningPlayer() {
        if (hasGameWinner()) {
            return getMoves().last().getPlayer();
        }

        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getWinningCondition() {
        return winningCondition;
    }

    public void setWinningCondition(int winningCondition) {
        this.winningCondition = winningCondition;
    }

    public RealmList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(RealmList<Player> players) {
        this.players = players;
    }

    public RealmList<Move> getMoves() {
        return moves;
    }

    public void setMoves(RealmList<Move> moves) {
        this.moves = moves;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
