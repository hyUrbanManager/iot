package com.hy.iot;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.things.pio.PeripheralManager;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "@MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logHardwareList();
    }

    private void logHardwareList() {
        PeripheralManager peripheralManager = PeripheralManager.getInstance();

        List<String> list = peripheralManager.getGpioList();
        Log.d(TAG, "gpio list: " + Arrays.toString(list.toArray()));

        list = peripheralManager.getPwmList();
        Log.d(TAG, "pwm list: " + Arrays.toString(list.toArray()));

        list = peripheralManager.getSpiBusList();
        Log.d(TAG, "spi list: " + Arrays.toString(list.toArray()));

        list = peripheralManager.getUartDeviceList();
        Log.d(TAG, "uart list: " + Arrays.toString(list.toArray()));

        list = peripheralManager.getI2cBusList();
        Log.d(TAG, "i2c list: " + Arrays.toString(list.toArray()));

    }
}
