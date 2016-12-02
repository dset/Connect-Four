package se.dset.android.connectfour;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private GridLayout board;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Random r = new Random();
        board = (GridLayout) findViewById(R.id.board);
        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColumnCount(); j++) {
                int color = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));
                BoardCell view = new BoardCell(this);
                view.setBackgroundColor(color);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(i), GridLayout.spec(j, 1.0f));
                params.width = 0;
                params.height = 0;
                view.setLayoutParams(params);
                board.addView(view);
            }
        }
    }
}
