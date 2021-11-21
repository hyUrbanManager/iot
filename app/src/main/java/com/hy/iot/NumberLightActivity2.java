package com.hy.iot;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hy.iot.data.ISinaStock;
import com.hy.iot.driver.LightController;

import retrofit2.Retrofit;

/**
 * 联想5个手指头的方法来表示数字
 *
 * @author huangye
 */
public class NumberLightActivity2 extends AppCompatActivity {

    private static final String TAG = "@NumberLight";

    private static final long PER_NUMBER_TIME = 3000;

    private static final String[] NUM_CODE = {
            "00000", "01000", "01100", "01110", "01111",
            "11111", "10001", "11000", "11100", "11110",
    };

    private static final String STOCK_CODE = "sz002841";

    // 根据摆放方向选择镜像显示
    private final boolean mIsMirror = true;

    TextView mTextView;

    private LightController mLightController;

    ISinaStock mSinaStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        mTextView = findViewById(R.id.text);
        mTextView.setText("利用5指头显示数字");

        mLightController = new LightController(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ISinaStock.BASE_URL)
                .build();
        mSinaStock = retrofit.create(ISinaStock.class);

        new Thread(() -> {
            while (true) {
                try {
                    String r = mSinaStock.getStock(STOCK_CODE).execute().body().string();
                    Log.d(TAG, "api: " + r);

                    float stock = Float.parseFloat(r.split(",")[1]);
                    Log.d(TAG, "stock: " + stock);

                    displayNum((int) (stock / 100));
                    sleep(PER_NUMBER_TIME);
                    allDown();
                    sleep(1000);

                    displayNum((int) (stock / 10));
                    sleep(PER_NUMBER_TIME);
                    allDown();
                    sleep(1000);

                    displayNum((int) (stock % 10));
                    sleep(PER_NUMBER_TIME);
                    allDown();
                    sleep(1000);

                    sleep(PER_NUMBER_TIME * 3);

                } catch (Exception e) {
                    e.printStackTrace();
                    allBlink();
                }
            }
        }).start();
    }


    // 0 - 9
    private void displayNum(int num) {
        char[] numCode = NUM_CODE[num].toCharArray();
        for (int i = 0; i < numCode.length; i++) {
            if (mIsMirror) {
                mLightController.setLight(numCode.length - 1 - i, numCode[i] == '1');
            } else {
                mLightController.setLight(i, numCode[i] == '1');
            }
        }
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

    private void allBlink() {
        for (int times = 0; times < 3; times++) {
            for (int i = 0; i < 5; i++) {
                mLightController.setLight(i, true);
            }
            sleep();
            for (int i = 0; i < 5; i++) {
                mLightController.setLight(i, false);
            }
            sleep();
        }
        sleep(PER_NUMBER_TIME * 3);
    }

    private void allDown() {
        for (int i = 0; i < 5; i++) {
            mLightController.setLight(i, false);
        }
    }

}
