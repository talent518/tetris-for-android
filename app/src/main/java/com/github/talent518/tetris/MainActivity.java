package com.github.talent518.tetris;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.talent518.tetris.view.PromptView;
import com.github.talent518.tetris.view.TetrisView;

public class MainActivity extends Activity {
    TetrisView tv;
    PromptView pv;
    TextView scores, lines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tetris);
        pv = findViewById(R.id.prompt);
        scores = findViewById(R.id.scores);
        lines = findViewById(R.id.lines);
    }
}
