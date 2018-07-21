package com.github.talent518.tetris.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeView extends TextView {

    public TimeView(Context context) {
        super(context);

        init();
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setText(dateFormat.format(new Date()));

            sendEmptyMessageDelayed(0, 1000);
        }
    };

    private void init() {
        mHandler.handleMessage(null);
    }

}
