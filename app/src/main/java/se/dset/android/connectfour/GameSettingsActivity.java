package se.dset.android.connectfour;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/* The activity where the users choose their names and colors. */
public class GameSettingsActivity extends AppCompatActivity {
    private static final String SAVED_P1_COLOR = "saved_p1_color";
    private static final String SAVED_P2_COLOR = "saved_p2_color";

    private EditText p1EditText;
    private EditText p2EditText;

    private Button p1ColorButton;
    private Button p2ColorButton;
    private int p1Color;
    private int p2Color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        p1EditText = (EditText) findViewById(R.id.name_player_1);
        p2EditText = (EditText) findViewById(R.id.name_player_2);

        p1ColorButton = (Button) findViewById(R.id.color_player_1);
        p2ColorButton = (Button) findViewById(R.id.color_player_2);

        if (savedInstanceState == null) {
            p1Color = Color.parseColor("#0072BB");
            p2Color = Color.parseColor("#FF4C3B");
        } else {
            p1Color = savedInstanceState.getInt(SAVED_P1_COLOR);
            p2Color = savedInstanceState.getInt(SAVED_P2_COLOR);
        }

        updateColors();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_P1_COLOR, p1Color);
        outState.putInt(SAVED_P2_COLOR, p2Color);
    }

    public void onStartButtonClicked(View view) {
        String player1Name = p1EditText.getText().toString();
        String player2Name = p2EditText.getText().toString();

        /* Make sure that the players have different names. */
        if (player1Name.equals(player2Name)) {
            player2Name += "-2";
        }

        ArrayList<String> players = new ArrayList<>(Arrays.asList(player1Name, player2Name));
        int[] colors = new int[]{p1Color, p2Color};

        Intent intent = new Intent(this, GameActivity.class);
        intent.putStringArrayListExtra(GameActivity.EXTRA_PLAYER_NAMES, players);
        intent.putExtra(GameActivity.EXTRA_PLAYER_COLORS, colors);
        startActivity(intent);
    }

    public void onColorP1Clicked(View view) {
        p1Color = getRandomColor();
        updateColors();
    }

    public void onColorP2Clicked(View view) {
        p2Color = getRandomColor();
        updateColors();
    }

    private int getRandomColor() {
        Random r = new Random();
        return Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }

    private void updateColors() {
        p1ColorButton.setBackgroundColor(p1Color);
        p2ColorButton.setBackgroundColor(p2Color);
    }
}
