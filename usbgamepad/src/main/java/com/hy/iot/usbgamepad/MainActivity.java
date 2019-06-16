package com.hy.iot.usbgamepad;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends AppCompatActivity {

    private static final int VENDOR_ID = 0x0001;
    private static final int PRODUCT_ID = 0x0001;

    private static final String ACTION_DEVICE_PERMISSION = "com.hy.iot.usbgamepad.USB_PERMISSION";

    private TextView mInfoTextView;

    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private UsbEndpoint mUsbEndpointIn;
    private UsbEndpoint mUsbEndpointOut;
    private UsbInterface mUsbInterface;
    private UsbDeviceConnection mUsbDeviceConnection;

    private BroadcastReceiver mUsbDeviceChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mInfoTextView.append("BroadcastReceiver in\n");

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                mInfoTextView.append("ACTION_USB_DEVICE_ATTACHED\n");
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                mInfoTextView.append("ACTION_USB_DEVICE_DETACHED\n");
            }
        }
    };

    private final BroadcastReceiver mUsbPermissionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mInfoTextView.append("BroadcastReceiver in\n");
            if (ACTION_DEVICE_PERMISSION.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (device != null) {
                        mInfoTextView.append("usb EXTRA_PERMISSION_GRANTED");
                    }
                } else {
                    mInfoTextView.append("usb EXTRA_PERMISSION_GRANTED null!!!");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_DEVICE_PERMISSION), 0);
        IntentFilter permissionFilter = new IntentFilter(ACTION_DEVICE_PERMISSION);
        registerReceiver(mUsbPermissionReceiver, permissionFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbDeviceChangeReceiver, usbFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mUsbDeviceChangeReceiver);
    }

    private void listUsbDevice() {
        HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
        for (UsbDevice device : deviceHashMap.values()) {
            mInfoTextView.append("\ndevice name: " + device.getDeviceName() + "\ndevice product name:"
                    + device.getProductName() + "\nvendor id:" + device.getVendorId() +
                    "\ndevice serial: " + device.getSerialNumber());
        }
    }

    private void checkPermission(UsbDevice device) {
        //先判断是否为自己的设备
        //注意：支持十进制和十六进制
        //比如：device.getProductId() == 0x04D2
        if (device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID) {
            if (mUsbManager.hasPermission(device)) {
                //do your work
            } else {
                mUsbManager.requestPermission(device, null);
            }
        }
    }

    private void initCommunication(UsbDevice device) {
        mInfoTextView.append("initCommunication in\n");
        if (VENDOR_ID == device.getVendorId() && PRODUCT_ID == device.getProductId()) {
            mInfoTextView.append("initCommunication in right device\n");
            int interfaceCount = device.getInterfaceCount();
            for (int interfaceIndex = 0; interfaceIndex < interfaceCount; interfaceIndex++) {
                UsbInterface usbInterface = device.getInterface(interfaceIndex);
                if ((UsbConstants.USB_CLASS_CDC_DATA != usbInterface.getInterfaceClass())
                        && (UsbConstants.USB_CLASS_COMM != usbInterface.getInterfaceClass())) {
                    continue;
                }

                for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                    UsbEndpoint ep = usbInterface.getEndpoint(i);
                    if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                            mUsbEndpointIn = ep;
                        } else {
                            mUsbEndpointOut = ep;
                        }
                    }
                }

                if (mUsbEndpointIn == null || mUsbEndpointOut == null) {
                    mInfoTextView.append("endpoint is null\n");
                    mUsbEndpointIn = null;
                    mUsbEndpointOut = null;
                    mUsbInterface = null;
                } else {
                    mInfoTextView.append("\nendpoint out: " + mUsbEndpointOut + ",endpoint in: " +
                            mUsbEndpointIn.getAddress() + "\n");
                    mUsbInterface = usbInterface;
                    mUsbDeviceConnection = mUsbManager.openDevice(device);
                    break;
                }
            }
        }
    }


}
