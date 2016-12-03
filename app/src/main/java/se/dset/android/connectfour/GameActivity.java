package se.dset.android.connectfour;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
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

public class GameActivity extends AppCompatActivity {
    private static final String SAVED_GAME_STATE_ID = "saved_game_state_id";

    private GridLayout board;
    private BoardCell[][] cells;

    private Realm realm;
    private String gameStateId;
    private GameState state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        board = (GridLayout) findViewById(R.id.board);
        cells = new BoardCell[board.getRowCount()][board.getColumnCount()];
        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColumnCount(); j++) {
                BoardCell view = new BoardCell(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(i), GridLayout.spec(j, 1.0f));
                params.width = 0;
                params.height = 0;
                view.setLayoutParams(params);
                board.addView(view);

                final int column = j;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        placeInColumn(column);
                    }
                });

                cells[board.getRowCount() - 1 - i][j] = view;
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
            GameState tmpState = new GameState(UUID.randomUUID().toString(), board.getRowCount(), board.getColumnCount(), 4, players);
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

        BoardCell cell = cells[move.getRow()][move.getColumn()];
        cell.setBackgroundColor(color);

        if (animate) {
            cell.setTranslationY(-1300f);
            cell.animate().translationY(0).setDuration(500).setInterpolator(new AccelerateInterpolator()).start();
        }
    }
}
