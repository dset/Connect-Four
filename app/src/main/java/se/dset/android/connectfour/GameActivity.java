package se.dset.android.connectfour;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
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
    public static final String EXTRA_PLAYER_NAMES = "extra_player_names";
    public static final String EXTRA_PLAYER_COLORS = "extra_player_colors";

    private static final String SAVED_GAME_STATE_ID = "saved_game_state_id";

    private int numRows;
    private int numColumns;
    private List<String> playerNames;
    private int[] playerColors;

    private BoardCellView[][] board;
    private Realm realm;
    private String gameStateId;
    private GameState state;

    private View indicator;
    private TextView status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        indicator = findViewById(R.id.indicator);
        status = (TextView) findViewById(R.id.status);

        numRows = getIntent().getIntExtra(EXTRA_NUM_ROWS, 6);
        numColumns = getIntent().getIntExtra(EXTRA_NUM_COLUMNS, 7);

        playerNames = getIntent().getStringArrayListExtra(EXTRA_PLAYER_NAMES);
        if (playerNames == null) {
            playerNames = Arrays.asList("Player 1", "Player 2");
        }

        playerColors = getIntent().getIntArrayExtra(EXTRA_PLAYER_COLORS);
        if (playerColors == null) {
            playerColors = new int[]{Color.parseColor("#4298b5"), Color.parseColor("#dd5f32")};
        }

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

        if (savedInstanceState == null) {
            Toast.makeText(this, R.string.toast_game_hint, Toast.LENGTH_LONG).show();
        } else {
            gameStateId = savedInstanceState.getString(SAVED_GAME_STATE_ID);
        }

        if (gameStateId == null) {
            newGameState();
        } else {
            realm.beginTransaction();
            state = realm.where(GameState.class).equalTo("id", gameStateId).findFirst();
            realm.commitTransaction();
        }

        for (Move move : state.getMoves()) {
            renderMove(move, false);
        }
        updateStatus();
    }

    private void newGameState() {
        realm.beginTransaction();

        Player player1 = realm.copyToRealmOrUpdate(new Player(playerNames.get(0)));
        Player player2 = realm.copyToRealmOrUpdate(new Player(playerNames.get(1)));
        RealmList<Player> players = new RealmList<>(player1, player2);
        GameState tmpState = new GameState(UUID.randomUUID().toString(), numRows, numColumns, 4, players);
        state = realm.copyToRealm(tmpState);

        realm.commitTransaction();

        gameStateId = state.getId();
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
        updateStatus();
    }

    private void renderMove(Move move, boolean animate) {
        int playerIndex = state.getPlayers().indexOf(move.getPlayer());
        int color = playerColors[playerIndex];

        BoardCellView cell = board[move.getRow()][move.getColumn()];
        PaintDrawable drawable = new PaintDrawable(color);
        drawable.setShape(new OvalShape());
        cell.setBackground(drawable);

        if (animate) {
            cell.setTranslationY(-1300f);
            cell.animate().translationY(0).setDuration(500).setInterpolator(new AccelerateInterpolator()).start();
        }
    }

    private void updateStatus() {
        if (state.isGameOver()) {
            if (state.hasGameWinner()) {
                Player winner = state.getWinningPlayer();
                status.setText(getString(R.string.status_win, winner.getName()));
                int color = playerColors[state.getPlayers().indexOf(winner)];
                indicator.setBackgroundColor(color);
            } else {
                status.setText(R.string.status_draw);
                indicator.setBackgroundColor(Color.WHITE);
            }
        } else {
            Player player = state.getCurrentPlayer();
            status.setText(getString(R.string.status_turn, player.getName()));
            int color = playerColors[state.getPlayers().indexOf(player)];
            indicator.setBackgroundColor(color);
        }
    }

    public void onRestartClicked(View view) {
        RestartDialogFragment dialog = new RestartDialogFragment();
        dialog.show(getSupportFragmentManager(), "restart_dialog");
    }

    public void restartGame() {
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                board[row][column].animate().cancel();
                board[row][column].setBackgroundColor(Color.TRANSPARENT);
            }
        }

        newGameState();
        updateStatus();
    }

    public static class RestartDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.dialog_restart_title)
                    .setMessage(R.string.dialog_restart_message)
                    .setPositiveButton(R.string.dialog_restart_positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GameActivity activity = (GameActivity) getActivity();
                            if (activity != null) {
                                activity.restartGame();
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialog_restart_negative, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
    }
}
