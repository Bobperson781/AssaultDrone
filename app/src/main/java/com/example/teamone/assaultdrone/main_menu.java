package com.example.teamone.assaultdrone;

//import com.example.teamone.assaultdrone.util.SystemUiHider;

//
//TODO
//
// Check to see if this can find bluetooth devices
// Change the scan to start on button_press
// Once synced with board/device, switch screen to the Beast screen
// Put the bluetooth connection stuff into Beast also
//
//TODO
//

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class main_menu extends Activity implements BluetoothAdapter.LeScanCallback {

    public static BluetoothDevice connectionDevice;
    private static final String TAG = "BluetoothGattActivity";

    private static final String DEVICE_NAME = "Beast";
    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;

    //TODO Make some custom UUIDs for our program
    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothGatt mConnectedGatt;
    private static ProgressDialog mProgress;
    private List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
    private ScanSettings scanSettings;

    Button playButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main_menu);
        setProgressBarIndeterminate(true);

		/*
		 * Bluetooth in Android 4.3 is accessed via the BluetoothManager, rather than
		 * the old static BluetoothAdapter.getInstance()
		 */
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();

        mDevices = new SparseArray<BluetoothDevice>();

		/*
		 * A progress dialog will be needed while the connection process is
		 * taking place
		 */
        mProgress = new ProgressDialog(this);
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
		/*
		 * We need to enforce that Bluetooth is first enabled, and take the
		 * user to settings to enable it if they have not done so.
		 */
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }

		/*
		 * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
		 * from installing on these devices, but this will allow test devices or other
		 * sideloads to report whether or not the feature exists.
		 */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //clearDisplayValues();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Make sure dialog is hidden
        mProgress.dismiss();
        //Cancel any scans in progress
        mHandler.removeCallbacks(mStopRunnable);
        mHandler.removeCallbacks(mStartRunnable);
        //mBluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
        mBluetoothAdapter.startLeScan((BluetoothAdapter.LeScanCallback) this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Disconnect from any active tag connection
        if (mConnectedGatt != null) {
            mConnectedGatt.disconnect();
            mConnectedGatt = null;
        }
    }


    // The onScreen click for the main menu
    public void onClick(View v) {
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.button_play:
                //TODO
                //Make this start the scan for the bluetooth connectivity
                playButtonClick();
                break;
            default:
                break;
        }
    }

    private void playButtonClick() {
        //mDevices.clear();
        startScan();
        //Intent i = new Intent(this, BeastMode.class);
        //startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add the "scan" option to the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_menu, menu);

        //Add any device elements we've discovered to the overflow menu
        for (int i = 0; i < mDevices.size(); i++) {
            BluetoothDevice device = mDevices.valueAt(i);
            menu.add(0, mDevices.keyAt(i), 0, device.getName());
        }

        return true;
    }

//    @Override
//    //TODO put this part into the button press
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_scan:
//                mDevices.clear();
//                startScan();
//                return true;
//            default:
//                //Obtain the discovered device to connect with
//                BluetoothDevice device = mDevices.get(item.getItemId());
//                Log.i(TAG, "Connecting to " + device.getName());
//			/*
//			 * Make a connection with the device using the special LE-specific
//			 * connectGatt() method, passing in a callback for GATT events
//			 */
//                mConnectedGatt = device.connectGatt(this, false, mGattCallback);
//                //Display progress UI
//                mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Connecting to " + device.getName() + "..."));
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    private void startScan() {
        //mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, scanCallback);
        mBluetoothAdapter.startLeScan((BluetoothAdapter.LeScanCallback) this);
        setProgressBarIndeterminateVisibility(true);

        mHandler.postDelayed(mStopRunnable, 3500);
    }

    private void stopScan() {
        //mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        mBluetoothAdapter.stopLeScan((BluetoothAdapter.LeScanCallback) this);
        setProgressBarIndeterminateVisibility(false);
    }

    //@Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.i(TAG, "New LE Device: " + device.getName() + " @ " + rssi);
		/*
		 * We are looking for Beast devices only, so validate the name
		 * that each device reports before adding it to our collection
		 */
        //TODO Make the server name = Beast, then undo if commenting
        if (DEVICE_NAME.equals(device.getName())) {
            //mDevices.put(device.hashCode(), device);
            //Update the overflow menu
            //invalidateOptionsMenu();
            //TODO connection with the bluetooth device is pushed off to the Beast activity
            connectionDevice = device;
            stopScan();
            Intent i = new Intent(main_menu.this, BeastMode.class);
            startActivity(i);
//            Log.i(TAG, "Connecting to " + device.getName());
//            mConnectedGatt = device.connectGatt(this, false, mGattCallback);
//            //Display progress UI
//            mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Connecting to " + device.getName() + "..."));
        }

    }

    public static synchronized BluetoothDevice getBtDevice() {
        if (connectionDevice == null) {
            // construct a BluetoothDevice object and put it into variable device
            Log.i(TAG, "Connection device is null");
        }
        return connectionDevice;
    }

    /*
     * In this callback, we've created a bit of a state machine to enforce that only
     * one characteristic be read or written at a time until all of our sensors
     * are enabled and we are registered to get notifications.
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /* State Machine Tracking */
        private int mState = 0;

        private void reset() {
            mState = 0;
        }

        private void advance() {
            mState++;
        }

        /*
         * Send an enable command to each sensor by writing a configuration
         * characteristic.  This is specific to the SensorTag to keep power
         * low by disabling sensors you aren't using.
         */
        //TODO This is where it sends data to the board
//        private void enableNextSensor(BluetoothGatt gatt) {
//            BluetoothGattCharacteristic characteristic = null;
//
//            switch (mState) {
//                case 0:
//                    Log.d(TAG, "Enabling pressure cal");
//                    //characteristic = gatt.getService(PRESSURE_SERVICE).getCharacteristic(PRESSURE_CONFIG_CHAR);
//                    characteristic.setValue(new byte[]{0x02});
//                    break;
//                case 1:
//                    Log.d(TAG, "Enabling pressure");
//                    //characteristic = gatt.getService(PRESSURE_SERVICE).getCharacteristic(PRESSURE_CONFIG_CHAR);
//                    characteristic.setValue(new byte[]{0x01});
//                    break;
//                case 2:
//                    Log.d(TAG, "Enabling humidity");
//                    //characteristic = gatt.getService(HUMIDITY_SERVICE).getCharacteristic(HUMIDITY_CONFIG_CHAR);
//                    characteristic.setValue(new byte[]{0x01});
//                    break;
//                default:
//                    mHandler.sendEmptyMessage(MSG_DISMISS);
//                    Log.i(TAG, "All Sensors Enabled");
//                    return;
//            }
//
//            gatt.writeCharacteristic(characteristic);
//        }


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "Connection State Change: "+status+" -> "+connectionState(newState));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
				/*
				 * Once successfully connected, we must next discover all the services on the
				 * device before we can read and write their characteristics.
				 */
                //TODO make the transition over to the Beast activity screen, move the rest of the BLE stuff there
                Intent i = new Intent(main_menu.this, BeastMode.class);
                startActivity(i);
                //gatt.discoverServices();
                //mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Discovering Services..."));
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
				/*
				 * If at any point we disconnect, send a message to clear the weather values
				 * out of the UI
				 */
                mHandler.sendEmptyMessage(MSG_CLEAR);
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
				/*
				 * If there is a failure at any stage, simply disconnect
				 */
                gatt.disconnect();
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.d(TAG, "Remote RSSI: "+rssi);
        }

        private String connectionState(int status) {
            switch (status) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "Connected";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "Disconnected";
                case BluetoothProfile.STATE_CONNECTING:
                    return "Connecting";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "Disconnecting";
                default:
                    return String.valueOf(status);
            }
        }
    };

    private static final int MSG_PROGRESS = 201;
    private static final int MSG_DISMISS = 202;
    private static final int MSG_CLEAR = 301;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_PROGRESS:
                    mProgress.setMessage((String) msg.obj);
                    if (!mProgress.isShowing()) {
                        mProgress.show();
                    }
                    break;
                case MSG_DISMISS:
                    mProgress.hide();
                    break;
                default:
                    break;
            }
        }
    };
}