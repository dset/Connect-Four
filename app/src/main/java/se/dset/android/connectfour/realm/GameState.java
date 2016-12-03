package se.dset.android.connectfour.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class GameState extends RealmObject {
    private int rows;
    private int columns;
    private int winningCondition;
    private RealmList<Player> players;
    private RealmList<Move> moves;

    public GameState() {
    }

    public GameState(int rows, int columns, int winningCondition, RealmList<Player> players) {
        this.rows = rows;
        this.columns = columns;
        this.winningCondition = winningCondition;
        this.players = players;
        this.moves = new RealmList<>();
    }

    public RealmList<Player> getPlayers() {
        return players;
    }

    public RealmList<Move> getMoves() {
        return moves;
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
        getMoves().add(move);

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
}
