package com.github.talent518.tetris.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.github.talent518.tetris.R;

import java.util.ArrayList;

public class TetrisView extends View {
    private static final String TAG = TetrisView.class.getSimpleName();
    private static final int MSG_PLAY = 0;
    private static final int MSG_ANIM = 1;

    private Resources res;

    private float textSize;
    private int bgColor, borderColor, gridColor, textColor, textShadowColor;
    private int blockSize;

    private PromptView pv;

    private Listener mListener;
    private boolean[][] shape = new boolean[4][4], tempShape = new boolean[4][4];
    private int shapeLeftTopColor, shapeColor, shapeRightBottomColor;
    private int shapeX, shapeY;

    private Shape[][] shapes = new Shape[18][10];
    private boolean isPlaying, isPaused, isStoped;
    private long mDelay, mDelayAnim = 42, mAnimAngle = 0, mStep = 15;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PLAY:
                    down();
                    break;
                case MSG_ANIM:
                    mAnimAngle += mStep;
                    if (mAnimAngle < 0 || mAnimAngle > 720) {
                        mStep = -mStep;
                        mAnimAngle += mStep;
                    }
                    sendEmptyMessageDelayed(MSG_ANIM, mDelayAnim);
                    invalidate();
                    break;
            }
        }
    };

    public TetrisView(Context context) {
        super(context);

        init();
    }

    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TetrisView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public TetrisView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        blockSize = height / 18;

        Log.i(TAG, "height = " + height + ", blockSize = " + blockSize);

        setMeasuredDimension(blockSize * 10, blockSize * 18);
    }

    private void init() {
        res = getResources();

        bgColor = res.getColor(R.color.box_bg);
        borderColor = res.getColor(R.color.box_border);
        gridColor = res.getColor(R.color.box_grid);
        textColor = res.getColor(R.color.tetris);
        textShadowColor = res.getColor(R.color.tetrisShadow);
        textSize = res.getDimension(R.dimen.tetris);

        for (int y = 0; y < 18; y++) {
            for (int x = 0; x < 10; x++) {
                shapes[y][x] = new Shape();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(bgColor);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(gridColor);

        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);

        for (int i = 0; i <= 18; i++) {
            int top = i * blockSize;
            if (i == 0 || i == 18) {
                if (i == 18) top--;
                canvas.drawLine(0, top, getWidth() + 1, top, borderPaint);
            } else {
                canvas.drawLine(0, top, getWidth(), top, paint);
            }
        }
        for (int i = 0; i <= 10; i++) {
            int left = i * blockSize;
            if (i == 0 || i == 10) {
                if (i == 10) left--;
                canvas.drawLine(left, 0, left, getHeight(), borderPaint);
            } else {
                canvas.drawLine(left, 1, left, getHeight() - 1, paint);
            }
        }

        for (int y = 0; y < 18; y++) {
            for (int x = 0; x < 10; x++) {
                if (shapes[y][x].isShape()) {
                    drawShape(canvas, x, y, shapes[y][x].getColor(), shapes[y][x].getLeftTopColor(), shapes[y][x].getRightBottomColor());
                }
            }
        }

        if (isPlaying) {
            for (int y = shapeY; y < shapeY + 4; y++) {
                for (int x = shapeX; x < shapeX + 4; x++) {
                    if (shape[y - shapeY][x - shapeX] && y >= 0) {
                        drawShape(canvas, x, y, shapeColor, shapeLeftTopColor, shapeRightBottomColor);
                    }
                }
            }
        }

        if (isStoped) {
            double rad = mAnimAngle * Math.PI / 180.0f, radius = rad, dx = Math.cos(rad) * radius, dy = Math.sin(rad) * radius;
            Paint textPaint = new Paint();
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setAntiAlias(true);
            textPaint.setFakeBoldText(true);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setShadowLayer((float) (radius / 2.0f), (float) dx, (float) dy, textShadowColor);
            canvas.drawText(res.getString(R.string.stoped), getWidth() / 2.0f, getHeight() / 2.0f + textSize / 3.0f, textPaint);
        }
    }

    private void drawShape(Canvas canvas, int x, int y, int color, int leftTopColor, int rightBottomColor) {
        Paint shapePaint = new Paint();
        shapePaint.setStyle(Paint.Style.FILL);
        shapePaint.setColor(color);

        Paint shapeLeftTopPaint = new Paint();
        shapeLeftTopPaint.setStyle(Paint.Style.STROKE);
        shapeLeftTopPaint.setColor(leftTopColor);

        Paint shapeRightBottomPaint = new Paint();
        shapeRightBottomPaint.setStyle(Paint.Style.STROKE);
        shapeRightBottomPaint.setColor(rightBottomColor);

        RectF rectF = new RectF(0, 0, blockSize - 1, blockSize - 1);
        rectF.offsetTo(x * blockSize, y * blockSize);

        canvas.drawRect(rectF, shapePaint);
        canvas.drawLine(rectF.left, rectF.top, rectF.left, rectF.bottom, shapeLeftTopPaint);
        canvas.drawLine(rectF.left, rectF.top, rectF.right, rectF.top, shapeLeftTopPaint);
        canvas.drawLine(rectF.right, rectF.top, rectF.right, rectF.bottom + 1, shapeRightBottomPaint);
        canvas.drawLine(rectF.left, rectF.bottom, rectF.right + 1, rectF.bottom, shapeRightBottomPaint);
    }

    public void setPromptView(PromptView v) {
        pv = v;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void newGame() {
        Log.i(TAG, "newGame");

        isPlaying = true;
        isPaused = true;
        isStoped = false;
        mDelay = 1000;
        mAnimAngle = 0;
        mStep = Math.abs(mStep);
        mHandler.removeMessages(MSG_ANIM);

        for (int y = 0; y < 18; y++) {
            for (int x = 0; x < 10; x++) {
                shapes[y][x].reset();
            }
        }

        nextShape();
        invalidate();
        mListener.newGame();
    }

    public void playGame() {
        if (isPlaying && isPaused) {
            Log.i(TAG, "playGame");

            isPaused = false;
            mHandler.sendEmptyMessageDelayed(MSG_PLAY, mDelay);
            invalidate();
            mListener.playGame();
        }
    }

    public boolean pauseGame() {
        if (!isPlaying || isPaused) {
            return false;
        }

        Log.i(TAG, "pauseGame");

        isPaused = true;
        mHandler.removeMessages(MSG_PLAY);
        invalidate();

        mListener.pauseGame();

        return true;
    }

    public void fall() {
        if (!isPlaying || isPaused) return;

        Log.i(TAG, "fall");

        while (isMovable(shapeX, shapeY + 1)) {
            shapeY++;
        }

        saveAndNextShape();

        mHandler.removeMessages(MSG_PLAY);
        mHandler.sendEmptyMessageDelayed(MSG_PLAY, mDelay);

        invalidate();
    }

    public void rotation() {
        if (!isPlaying || isPaused) return;

        rotateShape(tempShape, shape, 1);
        if (!isMovable(shapeX, shapeY, tempShape)) {
            return;
        }
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                shape[y][x] = tempShape[y][x];
            }
        }

        Log.i(TAG, "rotation");
        invalidate();
    }

    private void rotateShape(boolean[][] dstShape, boolean[][] srcShape, int grade) {
        int x, y;

        switch (grade % 4) {
            case 1:
                for (y = 0; y < 4; y++) {
                    for (x = 0; x < 4; x++) {
                        dstShape[x][3 - y] = srcShape[y][x];
                    }
                }
                break;
            case 2:
                for (y = 0; y < 4; y++) {
                    for (x = 0; x < 4; x++) {
                        dstShape[3 - y][3 - x] = srcShape[y][x];
                    }
                }
                break;
            case 3:
                for (y = 0; y < 4; y++) {
                    for (x = 0; x < 4; x++) {
                        dstShape[3 - x][y] = srcShape[y][x];
                    }
                }
                break;
            default:
                for (y = 0; y < 4; y++) {
                    for (x = 0; x < 4; x++) {
                        dstShape[y][x] = srcShape[y][x];
                    }
                }
                return;
        }

        if (dstShape[0][0]) {
            // Log.i(TAG, "rotateShape: dstShape = " + Arrays.deepToString(dstShape));
            return;
        }

        boolean[] arrX = new boolean[4], arrY = new boolean[4];
        for (y = 0; y < 4; y++) {
            for (x = 0; x < 4; x++) {
                arrX[x] |= dstShape[y][x];
                arrY[y] |= dstShape[y][x];
            }
        }

        // Log.i(TAG, "rotateShape: dstShape = " + Arrays.deepToString(dstShape) + "\narrX = " + Arrays.toString(arrX) + "\narrY = " + Arrays.toString(arrY));

        int sX = 0;
        for (x = 0; x < 4; x++) {
            if (arrX[x]) {
                break;
            }
            sX++;
        }
        int sY = 0;
        for (y = 0; y < 4; y++) {
            if (arrY[y]) {
                break;
            }
            sY++;
        }
        if (sX == 0 && sY == 0) {
            return;
        }
        for (y = 0; y < 4; y++) {
            for (x = 0; x < 4; x++) {
                dstShape[y][x] = (y + sY < 4 && x + sX < 4 ? dstShape[y + sY][x + sX] : false);
            }
        }

        // Log.i(TAG, "rotateShape: dstShape = " + Arrays.deepToString(dstShape));
    }

    public void down() {
        if (!isPlaying || isPaused) return;

        Log.i(TAG, "down");

        if (isMovable(shapeX, shapeY + 1)) {
            shapeY++;
        } else {
            saveAndNextShape();
        }

        mHandler.removeMessages(MSG_PLAY);
        mHandler.sendEmptyMessageDelayed(MSG_PLAY, mDelay);

        invalidate();
    }

    public void left() {
        if (!isPlaying || isPaused || !isMovable(shapeX - 1, shapeY)) return;

        Log.i(TAG, "left");

        shapeX--;
        invalidate();
    }

    public void right() {
        if (!isPlaying || isPaused || !isMovable(shapeX + 1, shapeY)) return;

        Log.i(TAG, "right");

        shapeX++;
        invalidate();
    }

    private boolean isMovable(int X, int Y) {
        return isMovable(X, Y, shape);
    }

    private boolean isMovable(int X, int Y, boolean[][] shape) {
        for (int y = Y; y < Y + 4; y++) {
            for (int x = X; x < X + 4; x++) {
                if (shape[y - Y][x - X] && (x < 0 || x >= 10 || y >= 18 || y >= 0 && shapes[y][x].isShape())) {
                    return false;
                }
            }
        }
        return true;
    }

    private void saveAndNextShape() {
        Log.i(TAG, "saveAndNextShape");

        ArrayList<Integer> stack = new ArrayList<>();
        boolean flag;

        for (int y = shapeY; y < shapeY + 4; y++) {
            flag = false;

            for (int x = shapeX; x < shapeX + 4; x++) {
                if (shape[y - shapeY][x - shapeX]) {
                    flag = true;

                    if (y < 0) {
                        isPlaying = false;
                        isStoped = true;
                    } else {
                        shapes[y][x].shape(true).color(shapeColor).leftTopColor(shapeLeftTopColor).rightBottomColor(shapeRightBottomColor);
                    }
                }
            }

            if (flag && y >= 0) {
                for (int x = 0; x < 10; x++) {
                    if (!shapes[y][x].isShape()) {
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    stack.add(y);
                }
            }
        }


        if (isPlaying) {
            if (stack.size() > 0) {
                int lines = mListener.linesChanged(stack.size());

                mDelay = Math.max(100, 1000 - lines * 9 / 8);

                int i = stack.size() - 1, moves = 1, y = stack.get(i);
                do {
                    while (i > 0 && y - moves == stack.get(i - 1)) {
                        i--;
                        moves++;
                    }

                    for (int x = 0; x < 10; x++) {
                        if (y - moves < 0) {
                            shapes[y][x].reset();
                        } else {
                            shapes[y][x].shape(shapes[y - moves][x]);
                        }
                    }

                    y--;
                } while (y >= 0);
            }

            nextShape();

            if (!isMovable(shapeX, shapeY + 1)) {
                isPlaying = false;
                isStoped = true;
            }
        }

        if (isStoped) {
            mHandler.sendEmptyMessageDelayed(MSG_ANIM, mDelayAnim);

            mListener.overGame();
        }
    }

    private void nextShape() {
        Log.i(TAG, "nextShape");

        boolean[][] shape = pv.getShape();
        shapeColor = pv.getShapeColor();
        shapeLeftTopColor = pv.getShapeLeftTopColor();
        shapeRightBottomColor = pv.getShapeRightBottomColor();

        int minX = 3, maxX = 0, Y = 0;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                this.shape[y][x] = shape[y][x];
                if (shape[y][x]) {
                    Y = y;
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                }
            }
        }

        shapeX = 5 - (minX + maxX + 1) / 2;
        shapeY = -Y - 1;

        pv.nextShape();

        invalidate();
    }

    class Shape {
        private boolean shape;
        private int color, leftTopColor, rightBottomColor;

        public Shape() {
            reset();
        }

        public void reset() {
            shape = false;
            color = Color.TRANSPARENT;
            leftTopColor = Color.TRANSPARENT;
            rightBottomColor = Color.TRANSPARENT;
        }

        public boolean isShape() {
            return shape;
        }

        public int getColor() {
            return color;
        }

        public int getLeftTopColor() {
            return leftTopColor;
        }

        public int getRightBottomColor() {
            return rightBottomColor;
        }

        public Shape shape(Shape shape) {
            return shape(shape.isShape()).color(shape.color).leftTopColor(shape.leftTopColor).rightBottomColor(shape.rightBottomColor);
        }

        public Shape shape(boolean shape) {
            this.shape = shape;
            return this;
        }

        public Shape color(int color) {
            this.color = color;
            return this;
        }

        public Shape leftTopColor(int leftTopColor) {
            this.leftTopColor = leftTopColor;
            return this;
        }

        public Shape rightBottomColor(int rightBottomColor) {
            this.rightBottomColor = rightBottomColor;
            return this;
        }
    }

    public interface Listener {
        void newGame();

        void playGame();

        void pauseGame();

        void overGame();

        int linesChanged(int lines);
    }
}
