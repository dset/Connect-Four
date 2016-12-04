package se.dset.android.connectfour;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import se.dset.android.connectfour.realm.GameState;

public class HighScoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        ListView highScore = (ListView) findViewById(R.id.high_score);
        highScore.setEmptyView(findViewById(R.id.empty_view));

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Map<String, Integer> nameToWins = new HashMap<>();

        RealmResults<GameState> states = realm.where(GameState.class).findAll();
        for (GameState state : states) {
            if (state.hasGameWinner()) {
                String winner = state.getWinningPlayer().getName();
                Integer wins = nameToWins.get(winner);
                nameToWins.put(winner, wins == null ? 1 : wins + 1);
            }
        }

        realm.commitTransaction();
        realm.close();

        List<Score> scores = new ArrayList<>();
        for (String player : nameToWins.keySet()) {
            scores.add(new Score(player, nameToWins.get(player)));
        }

        Collections.sort(scores, new Comparator<Score>() {
            @Override
            public int compare(Score s1, Score s2) {
                return s2.getWins() - s1.getWins();
            }
        });

        highScore.setAdapter(new HighScoreAdapter(this, scores));
    }

    private static class Score {
        private String player;
        private int wins;

        public Score(String player, int wins) {
            this.player = player;
            this.wins = wins;
        }

        public String getPlayer() {
            return player;
        }

        public int getWins() {
            return wins;
        }
    }

    private static class HighScoreAdapter extends ArrayAdapter<Score> {
        public HighScoreAdapter(Context context, List<Score> scores) {
            super(context, -1, scores);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Score score = getItem(position);
            View view = View.inflate(getContext(), R.layout.view_high_score_item, null);
            ((TextView) view.findViewById(R.id.name)).setText(score.getPlayer());
            ((TextView) view.findViewById(R.id.score)).setText("" + score.getWins());
            return view;
        }
    }
}
