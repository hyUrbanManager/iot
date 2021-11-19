package com.hy.iot;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hy.iot.driver.LightController;

import java.util.Random;

/**
 * @author huangye
 */
public class NumberLightActivity extends AppCompatActivity {

    private static final String TAG = "@NumberLight";

    private static final long PER_NUMBER_TIME = 1000;

    TextView mTextView;

    private LightController mLightController;

    private Random mRandom = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        mTextView = findViewById(R.id.text);
        mTextView.setText("利用闪灯显示数字");

        mLightController = new LightController(this);


        new Thread(() -> {
            while (true) {
                Log.d(TAG, "display");

                displayNum(mRandom.nextInt(10), 0);
                sleep(PER_NUMBER_TIME);
                displayNum(mRandom.nextInt(10), 1);
                sleep(PER_NUMBER_TIME);
                displayNum(mRandom.nextInt(10), 2);
                sleep(PER_NUMBER_TIME);
                displayNum(mRandom.nextInt(10), 3);
                sleep(PER_NUMBER_TIME);
                displayNum(mRandom.nextInt(10), 4);
                sleep(PER_NUMBER_TIME);

                sleep(PER_NUMBER_TIME);
                sleep(PER_NUMBER_TIME);
            }
        }).start();
    }


    private void displayNum(int num, int index) {
        mLightController.setLight(index, false);
        sleep();

        for (int i = 0; i < num; i++) {
            mLightController.setLight(index, true);
            sleep();
            mLightController.setLight(index, false);
            sleep();
        }

        mLightController.setLight(index, false);
        sleep();
    }

    private void sleep() {
        sleep(150);
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
