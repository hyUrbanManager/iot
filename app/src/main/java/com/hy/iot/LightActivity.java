package com.hy.iot;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author huangye
 */
public class LightActivity extends AppCompatActivity {

    private static final String TAG = "@LightActivity";

    private static final long INTERNAL = 100L;
    private static final long MAX_INDEX = 5;

    Button mButton1;
    Button mButton2;

    private LightController mLightController;

    private final Handler mMainHandler = new Handler();
    private Runnable mRunLightRunnable;
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        mLightController = new LightController(this);

        mButton1 = findViewById(R.id.button);
        mButton2 = findViewById(R.id.button2);

        mRunLightRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < MAX_INDEX; i++) {
//                    mLightController.setLight(i, mIndex == i);
                    mLightController.setLight(i, !(mIndex == i));
                }
                mIndex++;
                if (mIndex >= MAX_INDEX) {
                    mIndex = 0;
                }
                mMainHandler.postDelayed(mRunLightRunnable, INTERNAL);
            }
        };

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunLightRunnable.run();
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainHandler.removeCallbacksAndMessages(null);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int index = event.getKeyCode() % 5;
        mLightController.setLight(index, event.getAction() == KeyEvent.ACTION_DOWN);
        return super.dispatchKeyEvent(event);
    }
}
