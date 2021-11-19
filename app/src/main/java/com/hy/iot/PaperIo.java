package com.hy.iot;


import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

/**
 * paper io
 *
 * @author huangye
 */
public class PaperIo {

    private static final String TAG = "@PaperIO";

    private final PeripheralManager mPeripheralManager;

    private Gpio mRst;
    private Gpio mCs;
    private Gpio mSda;
    private Gpio mSclk;
    private Gpio mDc;

    public PaperIo() {
        mPeripheralManager = PeripheralManager.getInstance();

        try {
            mRst = mPeripheralManager.openGpio("BCM7");
            mRst.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            mCs= mPeripheralManager.openGpio("BCM25");
            mCs.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            mSda= mPeripheralManager.openGpio("BCM23");
            mSda.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            mSclk= mPeripheralManager.openGpio("BCM24");
            mSclk.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            mDc= mPeripheralManager.openGpio("BCM8");
            mDc.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (Exception e) {
            Log.e(TAG, "paper io error", e);
        }
    }

    public void nRST_H() {
        try {
            mRst.setValue(true);
        } catch (Exception e) {
            Log.e(TAG, "error.");
        }
    }

    public void nRST_L() {
        try {
            mRst.setValue(false);
        } catch (Exception e) {
            Log.e(TAG, "error.");
        }
    }

    public void nCS_H() {
        try {
            mCs.setValue(true);
        } catch (Exception e) {
            Log.e(TAG, "error.");
        }
    }

    public void nCS_L() {
        try {
            mCs.setValue(false);
        } catch (Exception e) {
            Log.e(TAG, "error.");
        }
    }

    public void SDA_H() {
        try {
            mSda.setValue(true);
        } catch (Exception e) {
            Log.e(TAG, "error.");
        }
    }

    public void SDA_L() {
        try {
            mSda.setValue(false);
        } catch (Exception e) {
            Log.e(TAG, "error.");
        }
    }

    public void SCLK_H() {
        try {
            mSclk.setValue(true);
        } catch (Exception e) {
            Log.e(TAG, "error.");
        }
    }

    public void SCLK_L() {
        try {
            mSclk.setValue(false);
        } catch (Exception e) {
            Log.e(TAG, "error.");
        }
    }

    public void nDC_H() {
        try {
            mDc.setValue(true);
        } catch (Exception e) {
            Log.e(TAG, "error.");
        }
    }

    public void nDC_L() {
        try {
            mDc.setValue(false);
        } catch (Exception e) {
            Log.e(TAG, "error.");
        }
    }

    public void nBUSY() {

    }
}
