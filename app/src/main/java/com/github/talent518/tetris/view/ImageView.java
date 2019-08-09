package com.github.talent518.tetris.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class ImageView extends android.widget.ImageView {
	public ImageView(Context context) {
		this(context, null);
	}

	public ImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}

	public ImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);

		setClickable(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(width, width);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.v(ImageView.class.getSimpleName(), "w = " + w + ", h = " + h);
		super.onSizeChanged(w, h, oldw, oldh);
	}
}
