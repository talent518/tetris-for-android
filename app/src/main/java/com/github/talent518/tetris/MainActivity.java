package com.github.talent518.tetris;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.talent518.tetris.view.PromptView;
import com.github.talent518.tetris.view.TetrisView;

public class MainActivity extends Activity implements TetrisView.Listener, View.OnClickListener, View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	GestureDetector mGestureDetector;
	private TetrisView tv;
	private PromptView pv;
	private TextView scores, scoresHighest, lines, linesHighest;
	private int nScores = 0, nScoresHighest = 0, nLines = 0, nLinesHighest = 0;
	private SharedPreferences preferences;
	private MenuDialog dialog;
	private LinearLayout leftCtrlPanel, rightCtrlPanel;

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

		leftCtrlPanel = findViewById(R.id.left_ctrl_panel);
		rightCtrlPanel = findViewById(R.id.right_ctrl_panel);

		pv = findViewById(R.id.prompt);
		tv = findViewById(R.id.tetris);
		tv.setPromptView(pv);
		tv.setListener(this);
		tv.setOnClickListener(this);
		tv.setOnTouchListener(this);
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
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getRealMetrics(dm);
		int blockSize = Math.round(dm.heightPixels / 19.0f);
		int gap = (dm.heightPixels - blockSize * 18) / 2;

		Log.i(TAG, "blockSize = " + blockSize + ", gap = " + gap + ", dm.heightPixels = " + dm.heightPixels);

		ViewGroup.MarginLayoutParams mlp;
		for (int i : marginIds) {
			View v = findViewById(i);
			mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			mlp.topMargin = gap;
			v.setLayoutParams(mlp);
		}

		mlp = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();
		mlp.rightMargin = gap;
		mlp.width = blockSize * 10;
		mlp.height = blockSize * 18;
		tv.setLayoutParams(mlp);

		mlp = (ViewGroup.MarginLayoutParams) pv.getLayoutParams();
		mlp.width = blockSize * 4;
		mlp.height = blockSize * 4;
		pv.setLayoutParams(mlp);

		mGestureDetector = new GestureDetector(this, this);
		mGestureDetector.setOnDoubleTapListener(this);

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

	@Override
	public void onClick(View v) {
		Log.i(TAG, "onClick");
		switch (v.getId()) {
			case R.id.left:
				tv.left();
				break;
			case R.id.right:
				tv.right();
				break;
			case R.id.up:
				tv.rotation();
				break;
			case R.id.down:
				tv.down();
				break;
			case R.id.ok:
				tv.fall();
				break;
			default:
				tv.fall();
				break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		float posX = e2.getX() - e1.getX();
		float posY = e2.getY() - e1.getY();
		float lenX = Math.abs(posX);
		float lenY = Math.abs(posY);

		Log.i(TAG, "posX = " + posX + ", posY = " + posY);

		if (posX > 0 && lenX > lenY) {
			Log.e(TAG, "onFling: Right");

			tv.right();
		} else if (posX < 0 && lenX > lenY) {
			Log.e(TAG, "onFling: Left");

			tv.left();
		} else if (posY > 0 && lenY > lenX) {
			Log.e(TAG, "onFling: Down");

			tv.down();
		} else if (posY < 0 && lenY > lenX) {
			Log.e(TAG, "onFling: Up");

			tv.rotation();
		} else
			return false;

		return true;
	}

	public class MenuDialog extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
		Button newGame, continueGame, quitGame;

		public MenuDialog(Context context) {
			super(context, R.style.MainDialog);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			getWindow().requestFeature(Window.FEATURE_NO_TITLE);

			setContentView(R.layout.dialog_main);

			setCancelable(false);
			setCanceledOnTouchOutside(false);
			setFinishOnTouchOutside(false);

			((CheckBox) findViewById(R.id.left_ctrl_panel)).setOnCheckedChangeListener(this);
			((CheckBox) findViewById(R.id.right_ctrl_panel)).setOnCheckedChangeListener(this);

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

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(buttonView.getId() == R.id.left_ctrl_panel) {
				leftCtrlPanel.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			} else {
				rightCtrlPanel.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			}
		}
	}
}
