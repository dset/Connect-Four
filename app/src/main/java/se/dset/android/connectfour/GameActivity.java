package se.dset.android.connectfour;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import java.util.Arrays;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import se.dset.android.connectfour.realm.GameState;
import se.dset.android.connectfour.realm.Move;
import se.dset.android.connectfour.realm.Player;
import se.dset.android.connectfour.view.BoardCellView;
import se.dset.android.connectfour.view.NoScrollGridView;

public class GameActivity extends AppCompatActivity {
    public static final String EXTRA_NUM_ROWS = "extra_num_rows";
    public static final String EXTRA_NUM_COLUMNS = "extra_num_columns";

    private static final String SAVED_GAME_STATE_ID = "saved_game_state_id";

    private BoardCellView[][] board;
    private Realm realm;
    private String gameStateId;
    private GameState state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        int numRows = getIntent().getIntExtra(EXTRA_NUM_ROWS, 6);
        int numColumns = getIntent().getIntExtra(EXTRA_NUM_COLUMNS, 7);

        NoScrollGridView boardLayout = (NoScrollGridView) findViewById(R.id.board_layout);
        boardLayout.setNumColumns(numColumns);
        board = new BoardCellView[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                BoardCellView view = new BoardCellView(this);
                boardLayout.addView(view);

                final int column = j;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        placeInColumn(column);
                    }
                });

                board[numRows - 1 - i][j] = view;
            }
        }

        realm = Realm.getDefaultInstance();

        if (savedInstanceState != null) {
            gameStateId = savedInstanceState.getString(SAVED_GAME_STATE_ID);
        }

        if (gameStateId == null) {
            realm.beginTransaction();

            Player player1 = realm.copyToRealmOrUpdate(new Player("Player 1"));
            Player player2 = realm.copyToRealmOrUpdate(new Player("Player 2"));
            RealmList<Player> players = new RealmList<>(player1, player2);
            GameState tmpState = new GameState(UUID.randomUUID().toString(), numRows, numColumns, 4, players);
            state = realm.copyToRealm(tmpState);

            realm.commitTransaction();

            gameStateId = state.getId();
        } else {
            realm.beginTransaction();
            state = realm.where(GameState.class).equalTo("id", gameStateId).findFirst();
            realm.commitTransaction();
        }

        for (Move move : state.getMoves()) {
            renderMove(move, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_GAME_STATE_ID, gameStateId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
        realm = null;
    }

    private void placeInColumn(int column) {
        Move move = state.placeInColumn(column);
        if (move == null) {
            return;
        }

        renderMove(move, true);

        if (state.isGameOver()) {
            if (state.hasGameWinner()) {
                Toast.makeText(this, state.getWinningPlayer().getName() + " is the winner!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "It's a draw!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void renderMove(Move move, boolean animate) {
        int playerIndex = state.getPlayers().indexOf(move.getPlayer());
        int color = Arrays.asList(Color.RED, Color.BLUE).get(playerIndex);

        BoardCellView cell = board[move.getRow()][move.getColumn()];
        cell.setBackgroundColor(color);

        if (animate) {
            cell.setTranslationY(-1300f);
            cell.animate().translationY(0).setDuration(500).setInterpolator(new AccelerateInterpolator()).start();
        }
    }
}
