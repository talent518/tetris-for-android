package com.github.talent518.tetris.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.github.talent518.tetris.R;

import java.util.Random;

public class PromptView extends View {
    private static final String TAG = PromptView.class.getSimpleName();
    private static final boolean I = true;
    private static final boolean O = false;
    private static final boolean[][][] shapes = new boolean[][][]{
        {
            {I, O, O, O},
            {I, O, O, O},
            {I, O, O, O},
            {I, O, O, O}
        },
        {
            {I, O, O, O},
            {I, O, O, O},
            {I, I, O, O},
            {O, O, O, O}
        },
        {
            {O, I, O, O},
            {O, I, O, O},
            {I, I, O, O},
            {O, O, O, O}
        },
        {
            {O, I, O, O},
            {I, I, I, O},
            {O, O, O, O},
            {O, O, O, O}
        },
        {
            {I, I, O, O},
            {I, I, O, O},
            {O, O, O, O},
            {O, O, O, O}
        },
        {
            {O, I, O, O},
            {I, I, O, O},
            {I, O, O, O},
            {O, O, O, O}
        },
        {
            {I, O, O, O},
            {I, I, O, O},
            {O, I, O, O},
            {O, O, O, O}
        }
    };
    private static final int[] colors = new int[]{
        0x00, 0x33, 0x66, 0x99, 0xcc, 0xff
    };

    private int bgColor, borderColor, gridColor;
    private boolean[][] shape;
    private int shapeLeftTopColor, shapeColor, shapeRightBottomColor;
    private int blockSize;
    private Random random = new Random();

    public PromptView(Context context) {
        super(context);

        init();
    }

    public PromptView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PromptView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public PromptView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

        setMeasuredDimension(blockSize * 4, blockSize * 4);
    }

    private void init() {
        Resources res = getResources();

        bgColor = res.getColor(R.color.box_bg);
        borderColor = res.getColor(R.color.box_border);
        gridColor = res.getColor(R.color.box_grid);

        nextShape();
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
        for (int i = 0; i <= 4; i++) {
            int top = i * blockSize;
            if (i == 0 || i == 4) {
                if (i == 4) top--;
                canvas.drawLine(0, top, getWidth() + 1, top, borderPaint);
                canvas.drawLine(top, 0, top, getHeight() + 1, borderPaint);
            } else {
                canvas.drawLine(1, top, getWidth(), top, paint);
                canvas.drawLine(top, 1, top, getHeight(), paint);
            }
        }

        Paint shapePaint = new Paint();
        shapePaint.setStyle(Paint.Style.FILL);
        shapePaint.setColor(shapeColor);

        Paint shapeLeftTopPaint = new Paint();
        shapeLeftTopPaint.setStyle(Paint.Style.STROKE);
        shapeLeftTopPaint.setColor(shapeLeftTopColor);

        Paint shapeRightBottomPaint = new Paint();
        shapeRightBottomPaint.setStyle(Paint.Style.STROKE);
        shapeRightBottomPaint.setColor(shapeRightBottomColor);

        RectF rectF = new RectF(0, 0, blockSize - 1, blockSize - 1);

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (shape[y][x]) {
                    rectF.offsetTo(x * blockSize, y * blockSize);
                    canvas.drawRect(rectF, shapePaint);
                    canvas.drawLine(rectF.left, rectF.top, rectF.left, rectF.bottom, shapeLeftTopPaint);
                    canvas.drawLine(rectF.left, rectF.top, rectF.right, rectF.top, shapeLeftTopPaint);
                    canvas.drawLine(rectF.right, rectF.top, rectF.right, rectF.bottom + 1, shapeRightBottomPaint);
                    canvas.drawLine(rectF.left, rectF.bottom, rectF.right + 1, rectF.bottom, shapeRightBottomPaint);
                }
            }
        }
    }

    public boolean[][] getShape() {
        return shape;
    }

    public int getShapeColor() {
        return shapeColor;
    }

    public int getShapeLeftTopColor() {
        return shapeLeftTopColor;
    }

    public int getShapeRightBottomColor() {
        return shapeRightBottomColor;
    }

    public void nextShape() {
        random.setSeed(System.currentTimeMillis());

        shape = shapes[random.nextInt(shapes.length)];

        int r, g, b;

        do {
            r = colors[random.nextInt(colors.length - 2) + 1];
            g = colors[random.nextInt(colors.length - 2) + 1];
            b = colors[random.nextInt(colors.length - 2) + 1];
        } while (r == g && r == b && r >= 0x99);

        shapeColor = Color.rgb(r, g, b);
        shapeLeftTopColor = Color.rgb(r + 0x33, g + 0x33, b + 0x33);
        shapeRightBottomColor = Color.rgb(r - 0x33, g - 0x33, b - 0x33);

        invalidate();
    }
}
