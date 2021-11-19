package com.hy.iot;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.hy.iot.driver.LightController;
import com.hy.iot.driver.PaperDisplay;

/**
 * @author huangye
 */
public class LightActivity extends AppCompatActivity {

    private static final String TAG = "@LightActivity";

    private static final long INTERNAL = 100L;
    private static final long MAX_INDEX = 5;

    Button mButton1;
    Button mButton2;
    Button mButton3;

    private LightController mLightController;

    private final Handler mMainHandler = new Handler();
    private Runnable mRunLightRunnable;
    private int mIndex;

    private PaperDisplay mPaperDisplay = new PaperDisplay();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        mLightController = new LightController(this);

        mButton1 = findViewById(R.id.button);
        mButton2 = findViewById(R.id.button2);
        mButton3 = findViewById(R.id.button3);

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

        mButton3.setOnClickListener(v -> new Thread(() -> {
            Log.d(TAG, "paper display.");
            mPaperDisplay.INIT_SSD1673();

            Log.d(TAG, "paper end.");

//            mPaperDisplay.Init_buff();
//            mPaperDisplay.DIS_IMG(PaperDisplay.PIC_WHITE);
//
//            mPaperDisplay.nRST_L();
//            mPaperDisplay.nCS_L();
//            mPaperDisplay.SDA_L();
//            mPaperDisplay.SCLK_H();
//            mPaperDisplay.nDC_L();


        }).start());
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int index = event.getKeyCode() % 5;
        mLightController.setLight(index, event.getAction() == KeyEvent.ACTION_DOWN);
        return super.dispatchKeyEvent(event);
    }
}
