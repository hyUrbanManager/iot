package com.hy.iot.driver;

import android.content.Context;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

/**
 * @author huangye
 */
public class LightController {

    private static final String TAG = "@LightController";

    private static final String[] LIGHTS = {
            "BCM5",
            "BCM6",
            "BCM13",
            "BCM19",
            "BCM26",
    };

    private Context mContext;
    private PeripheralManager mPeripheralManager;
    private final Gpio[] mGpios = new Gpio[LIGHTS.length];

    public LightController(Context context) {
        this.mContext = context;
        mPeripheralManager = PeripheralManager.getInstance();

        for (int i = 0; i < LIGHTS.length; i++) {
            try {
                Gpio gpio = mPeripheralManager.openGpio(LIGHTS[i]);
                gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                mGpios[i] = gpio;
            } catch (Exception e) {
                Log.e(TAG, "setLight error", e);
            }
        }
    }

    public void setLight(int index, boolean on) {
        try {
            mGpios[index].setValue(on);
        } catch (Exception e) {
            Log.e(TAG, "setLight error", e);
        }

    }

}
