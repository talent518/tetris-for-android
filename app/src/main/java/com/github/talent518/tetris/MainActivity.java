package com.github.talent518.tetris;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.github.talent518.tetris.view.PromptView;
import com.github.talent518.tetris.view.TetrisView;

public class MainActivity extends Activity implements TetrisView.Listener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TetrisView tv;
    private PromptView pv;
    private TextView scores, scoresHighest, lines, linesHighest;
    private int nScores = 0, nScoresHighest = 0, nLines = 0, nLinesHighest = 0;
    private SharedPreferences preferences;

    private MenuDialog dialog;

    private int[] marginIds = new int[]{
        R.id.scores_layout,
        R.id.scores_layout_highest,
        R.id.help,
        R.id.time
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        pv = findViewById(R.id.prompt);
        tv = findViewById(R.id.tetris);
        tv.setPromptView(pv);
        tv.setListener(this);
        scores = findViewById(R.id.scores);
        scoresHighest = findViewById(R.id.scores_highest);
        lines = findViewById(R.id.lines);
        linesHighest = findViewById(R.id.lines_highest);

        preferences = getSharedPreferences("highest", Context.MODE_PRIVATE);
        nScoresHighest = preferences.getInt("scores", 0);
        nLinesHighest = preferences.getInt("lines", 0);

        scoresHighest.setText(Integer.toString(nScoresHighest));
        linesHighest.setText(Integer.toString(nLinesHighest));

        // *********************************************************************************
        // 初始化间距
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int gap = (dm.heightPixels - Math.round(dm.heightPixels / 19.0f) * 18) / 2;

        Log.i(TAG, "gap = " + gap);

        ViewGroup.MarginLayoutParams mlp;
        for (int i : marginIds) {
            View v = findViewById(i);
            mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.topMargin = gap;
            v.setLayoutParams(mlp);
        }

        mlp = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();
        mlp.rightMargin = gap;
        tv.setLayoutParams(mlp);

        findViewById(R.id.main).setPadding(gap, gap, gap, gap);

        // *********************************************************************************
        // 初始化菜单对话框
        dialog = new MenuDialog(this);
        dialog.show();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_MENU:
                if (dialog.isShowing()) {
                    dialog.dismiss();
                } else {
                    dialog.show();
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
                tv.fall();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                tv.rotation();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                tv.down();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                tv.left();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                tv.right();
                break;
            default:
                return super.onKeyUp(keyCode, event);
        }

        return true;
    }

    @Override
    public void newGame() {
        nScores = 0;
        scores.setText("0");
        nLines = 0;
        lines.setText("0");
    }

    @Override
    public void playGame() {

    }

    @Override
    public void pauseGame() {

    }

    @Override
    public void overGame() {
        if (nScores <= nScoresHighest && nLines <= nLinesHighest) {
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("scores", nScores);
        editor.putInt("lines", nLines);
        editor.commit();

        nScoresHighest = nScores;
        nLinesHighest = nLines;

        scoresHighest.setText(Integer.toString(nScoresHighest));
        linesHighest.setText(Integer.toString(nLinesHighest));
    }

    @Override
    public int linesChanged(int n) {
        nScores += n * 2 - 1;
        nLines += n;

        scores.setText(Integer.toString(nScores));
        lines.setText(Integer.toString(nLines));

        return nLines;
    }

    public class MenuDialog extends Dialog implements View.OnClickListener {
        Button newGame, continueGame, quitGame;

        public MenuDialog(Context context) {
            super(context, R.style.MainDialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.dialog_main);

            newGame = findViewById(R.id.game_new);
            newGame.setOnClickListener(this);
            continueGame = findViewById(R.id.game_continue);
            continueGame.setOnClickListener(this);
            quitGame = findViewById(R.id.game_quit);
            quitGame.setOnClickListener(this);
        }

        @Override
        protected void onStart() {
            super.onStart();

            if (tv.pauseGame()) {
                continueGame.setFocusable(true);
                continueGame.setFocusableInTouchMode(true);
                continueGame.setEnabled(true);
                continueGame.requestFocus();
            } else {
                continueGame.setFocusable(false);
                continueGame.setFocusableInTouchMode(false);
                continueGame.setEnabled(false);
                newGame.requestFocus();
            }
        }

        @Override
        protected void onStop() {
            super.onStop();

            tv.playGame();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.game_new:
                    tv.newGame();
                    dismiss();
                    break;
                case R.id.game_continue:
                    dismiss();
                    break;
                case R.id.game_quit:
                    dismiss();
                    finish();
                    break;
            }
        }
    }
}
