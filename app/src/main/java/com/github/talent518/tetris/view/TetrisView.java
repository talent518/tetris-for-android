package com.github.talent518.tetris.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.github.talent518.tetris.R;

public class TetrisView extends View {
    private static final String TAG = TetrisView.class.getSimpleName();

    private int bgColor, borderColor, gridColor;
    private int blockSize;

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

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        blockSize = height / 18;

        Log.i(TAG, "height = " + height + ", blockSize = " + blockSize);

        setMeasuredDimension(blockSize * 10, blockSize * 18);
    }

    private void init() {
        Resources res = getResources();

        bgColor = res.getColor(R.color.box_bg);
        borderColor = res.getColor(R.color.box_border);
        gridColor = res.getColor(R.color.box_grid);
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
                canvas.drawLine(0, top, getWidth()+1, top, borderPaint);
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
                canvas.drawLine(left, 1, left, getHeight()-1, paint);
            }
        }
    }
}
