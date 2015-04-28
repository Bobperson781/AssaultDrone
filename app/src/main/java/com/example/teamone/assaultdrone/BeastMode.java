package com.example.teamone.assaultdrone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static java.lang.Math.abs;


public class BeastMode extends Activity {

    private BluetoothDevice connectionDevice = main_menu.getBtDevice();
    private static final String TAG = "BluetoothGattActivity";
    private static final String DEVICE_NAME = "Beast";
    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;
    private BluetoothGatt mConnectedGatt;
    private static ProgressDialog mProgress;
    //private byte [] panTilt = 0b0000;
    private boolean up_pressed = false;
    private boolean down_pressed = false;
    private boolean left_pressed = false;
    private boolean right_pressed = false;
    private boolean fire = false;
    private boolean laser = false;
    private int update = 0;
    private byte [] sendMe;

    Timer timer;
    updateAnalog myTimerTask;


    //TODO Make some custom UUIDs for our program
    private static final UUID BEAST_UUID = UUID.fromString("3000");//"bce9a160-ec89-11e4-b80c-0800200c9a66");
    private static final UUID LEFT_SKID = UUID.fromString("3A00");//"bce9a161-ec89-11e4-b80c-0800200c9a66");
    private static final UUID RIGHT_SKID = UUID.fromString("3A01");//"bce9a162-ec89-11e4-b80c-0800200c9a66");
    private static final UUID FIRE = UUID.fromString("3A02");//"bce9a163-ec89-11e4-b80c-0800200c9a66");
    private static final UUID LASER = UUID.fromString("3A03");//"bce9a164-ec89-11e4-b80c-0800200c9a66");
    private static final UUID PANTILT = UUID.fromString("3A04");//"bce9a165-ec89-11e4-b80c-0800200c9a66");
    //private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beast_mode);

        mProgress = new ProgressDialog(this);
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);

        connectionDevice = main_menu.getBtDevice();
        if(connectionDevice != null){
            mConnectedGatt = connectionDevice.connectGatt(this, false, mGattCallback);
            //mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Connecting to " + connectionDevice.getName() + "..."));

        }
        final TextView x=(TextView)findViewById(R.id.x);
        final TextView y=(TextView)findViewById(R.id.y);

        //TODO add multitouch sensing to the button detection?
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.beast);
        rl.addView(new com.example.teamone.assaultdrone.AnalogStick(this, x, y));

        timer = new Timer();
        myTimerTask = new updateAnalog();

        //delay 2000ms, repeat in 300ms
        timer.schedule(myTimerTask, 2000, 300);

        final Button fireButton = (Button) findViewById(R.id.Fire);
        fireButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //fire button was pressed
                    fire = true;
                    update = 0;
                    sendData(mConnectedGatt);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    fire = false;
                    update = 0;
                    sendData(mConnectedGatt);
                }
                return true;
            }
        });

        // Should send Direction int = 1000 (Binary)
        final ImageButton upBottom = (ImageButton) findViewById(R.id.Up);
        upBottom.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //up button was pressed
                    //panTilt = 0b1000;
                    up_pressed = true;
                    update = 2;
                    sendData(mConnectedGatt);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    //panTilt = 0b0000;
                    up_pressed = false;
                    update = 2;
                    sendData(mConnectedGatt);
                }
                return true;
            }
        });

        // Should send Direction int = 0100 (Binary)
        final ImageButton downBottom = (ImageButton) findViewById(R.id.Down);
        downBottom.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //down button was pressed
                    //panTilt = 0b0100;
                    down_pressed = true;
                    update = 3;
                    sendData(mConnectedGatt);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    //panTilt = 0b0000;
                    down_pressed = false;
                    update = 3;
                    sendData(mConnectedGatt);
                }
                return true;
            }
        });

        // Should send Direction int = 0010 (Binary)
        final ImageButton leftBottom = (ImageButton) findViewById(R.id.Left);
        leftBottom.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //left button was pressed
                    //panTilt = 0b0010;
                    left_pressed = true;
                    update = 4;
                    sendData(mConnectedGatt);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    //panTilt = 0b0000;
                    left_pressed = false;
                    update = 4;
                    sendData(mConnectedGatt);
                }
                return true;
            }
        });

        // Should send Direction int = 0001 (Binary)
        final ImageButton rightBottom = (ImageButton) findViewById(R.id.Right);
        rightBottom.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //right button was pressed
                    //panTilt = 0b0001;
                    right_pressed = true;
                    update = 5;
                    sendData(mConnectedGatt);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    //panTilt = 0b0000;
                    right_pressed = false;
                    update = 5;
                    sendData(mConnectedGatt);
                }
                return true;
            }
        });
    }

    // Should send a 1 until otherwise toggled
    public void LaserOnToggle(View v){
        ToggleButton button = (ToggleButton) v;
        switch (button.getId()) {
            case R.id.Laser:
                if(button.isChecked()){
                    laser = true;
                    update = 1;
                    //TODO make the call to send the data based on button press!
                   sendData(mConnectedGatt);
                }else{
                    laser = false;
                    update = 1;
                    sendData(mConnectedGatt);
                }
                break;
            default:
                break;
        }
    }

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

        if (DEVICE_NAME.equals(device.getName())) {
            //mDevices.put(device.hashCode(), device);
            //Update the overflow menu
            //invalidateOptionsMenu();
            connectionDevice = device;
            Log.i(TAG, "Connecting to " + device.getName());
            mConnectedGatt = device.connectGatt(this, false, mGattCallback);
            //Display progress UI
            mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Connecting to " + device.getName() + "..."));
        }

    }

    private BluetoothGattCallback mGattCallback;

    {
        mGattCallback = new BluetoothGattCallback() {

            /* State Machine Tracking */
            private int mState = 0;

            private void reset() {
                mState = 0;
            }

            private void advance() {
                mState++;
            }

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.d(TAG, "Connection State Change: " + status + " -> " + connectionState(newState));
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                /*
				 * Once successfully connected, we must next discover all the services on the
				 * device before we can read and write their characteristics.
				 */
                    //stopScan();

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

//        /*
//         * Read the data characteristic's value for each sensor explicitly
//         */
//        //TODO This is where it reads the data from the board
//        private void readNextSensor(BluetoothGatt gatt) {
//            BluetoothGattCharacteristic characteristic = null;
//            switch (mState) {
//                case 0:
//                    Log.d(TAG, "Reading pressure cal");
//                    // characteristic = gatt.getService(PRESSURE_SERVICE).getCharacteristic(PRESSURE_CAL_CHAR);
//                    break;
//                case 1:
//                    Log.d(TAG, "Reading pressure");
//                    //characteristic = gatt.getService(PRESSURE_SERVICE).getCharacteristic(PRESSURE_DATA_CHAR);
//                    break;
//                case 2:
//                    Log.d(TAG, "Reading humidity");
//                    //characteristic = gatt.getService(HUMIDITY_SERVICE).getCharacteristic(HUMIDITY_DATA_CHAR);
//                    break;
//                default:
//                    mHandler.sendEmptyMessage(MSG_DISMISS);
//                    Log.i(TAG, "All Sensors Enabled");
//                    return;
//            }
//
//            gatt.readCharacteristic(characteristic);
//        }
//
//        /*
//         * Enable notification of changes on the data characteristic for each sensor
//         * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
//         * configuration descriptor.
//         */
//        private void setNotifyNextSensor(BluetoothGatt gatt) {
//            BluetoothGattCharacteristic characteristic = null;
//            switch (mState) {
//                case 0:
//                    Log.d(TAG, "Set notify pressure cal");
//                    //characteristic = gatt.getService(PRESSURE_SERVICE).getCharacteristic(PRESSURE_CAL_CHAR);
//                    break;
//                case 1:
//                    Log.d(TAG, "Set notify pressure");
//                    //characteristic = gatt.getService(PRESSURE_SERVICE).getCharacteristic(PRESSURE_DATA_CHAR);
//                    break;
//                case 2:
//                    Log.d(TAG, "Set notify humidity");
//                    //characteristic = gatt.getService(HUMIDITY_SERVICE).getCharacteristic(HUMIDITY_DATA_CHAR);
//                    break;
//                default:
//                    mHandler.sendEmptyMessage(MSG_DISMISS);
//                    Log.i(TAG, "All Sensors Enabled");
//                    return;
//            }
//
//            //Enable local notifications
//            gatt.setCharacteristicNotification(characteristic, true);
//            //Enabled remote notifications
//            BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
//            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            gatt.writeDescriptor(desc);
//        }


//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            Log.d(TAG, "Services Discovered: "+status);
//            mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Enabling Sensors..."));
//			/*
//			 * With services discovered, we are going to reset our state machine and start
//			 * working through the sensors we need to enable
//			 */
//            reset();
//            //enableNextSensor(gatt);
//        }
//
//        @Override
//        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            //For each read, pass the data up to the UI thread to update the display
//            //            if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
//            //                mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
//            //            }
//            //            if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
//            //                mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE, characteristic));
//            //            }
//            //            if (PRESSURE_CAL_CHAR.equals(characteristic.getUuid())) {
//            //                mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE_CAL, characteristic));
//            //            }
//
//            //After reading the initial value, next we enable notifications
//            setNotifyNextSensor(gatt);
//        }
//
//        @Override
//        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            //After writing the enable flag, next we read the initial value
//            readNextSensor(gatt);
//        }
//
//        @Override
//        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//			/*
//			 * After notifications are enabled, all updates from the device on characteristic
//			 * value changes will be posted here.  Similar to read, we hand these up to the
//			 * UI thread to update the display.
//			 */
//            //            if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
//            //                mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
//            //            }
//            //            if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
//            //                mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE, characteristic));
//            //            }
//            //            if (PRESSURE_CAL_CHAR.equals(characteristic.getUuid())) {
//            //                mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE_CAL, characteristic));
//            //            }
//        }
//
//        @Override
//        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//            //Once notifications are enabled, we move to the next sensor and start over with enable
//            advance();
//            //enableNextSensor(gatt);
//        }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                Log.d(TAG, "Remote RSSI: " + rssi);
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
    }

    public void sendData(BluetoothGatt gatt) {
        BluetoothGattCharacteristic characteristic = null;

        switch (update) {
            case 0:// Firing update
                characteristic = gatt.getService(BEAST_UUID).getCharacteristic(FIRE);

                if (fire) {
                    characteristic.setValue(new byte[]{0x01});
                } else {
                    characteristic.setValue(new byte[]{0x00});
                }

                break;
            case 1:// Laser Update
                characteristic = gatt.getService(BEAST_UUID).getCharacteristic(LASER);

                if (laser) {
                    characteristic.setValue(new byte[]{0x01});
                } else {
                    characteristic.setValue(new byte[]{0x00});
                }

                break;
            case 2:// Up Button update
                characteristic = gatt.getService(BEAST_UUID).getCharacteristic(PANTILT);

                if (fire) {
                    characteristic.setValue(new byte[]{0x08});
                } else {
                    characteristic.setValue(new byte[]{0x00});
                }

                break;
            case 3:// Down Button update
                characteristic = gatt.getService(BEAST_UUID).getCharacteristic(PANTILT);

                if (fire) {
                    characteristic.setValue(new byte[]{0x04});
                } else {
                    characteristic.setValue(new byte[]{0x00});
                }

                break;
            case 4:// Left Button update
                characteristic = gatt.getService(BEAST_UUID).getCharacteristic(PANTILT);

                if (fire) {
                    characteristic.setValue(new byte[]{0x02});
                } else {
                    characteristic.setValue(new byte[]{0x00});
                }

                break;
            case 5:// Right Button update
                characteristic = gatt.getService(BEAST_UUID).getCharacteristic(PANTILT);

                if (fire) {
                    characteristic.setValue(new byte[]{0x01});
                } else {
                    characteristic.setValue(new byte[]{0x00});
                }

                break;
            default:
                return;
        }
        gatt.writeCharacteristic(characteristic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beast_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    //TODO
    // Updates the analog stick position as a signal across Bluetooth
    public class updateAnalog extends TimerTask {

        double x_value;
        double y_value;
        int posX;
        int posY;
        BluetoothGattCharacteristic left_characteristic = null;
        BluetoothGattCharacteristic right_characteristic = null;

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    //TODO do all of the X/Y analog control stuff here
                    byte forward_Mask = 0x20; // OR the bytes to set this direction bit
                    byte reverse_Mask = 0x1F; // AND the bytes with this to clear direction bit
                    x_value = AnalogStick.getXCor();
                    y_value = AnalogStick.getYCor();
                    posX = (int) abs(x_value);
                    posY = (int) abs(y_value);
                    left_characteristic = mConnectedGatt.getService(BEAST_UUID).getCharacteristic(LEFT_SKID);
                    right_characteristic = mConnectedGatt.getService(BEAST_UUID).getCharacteristic(RIGHT_SKID);
                    byte[] Left_bytes = new byte[1];
                    byte[] Right_bytes = new byte[1];
                    int corrected; // used to set the right or left lower, for turning

                    // Direction = 1, L=R=Y Speed
                    if((y_value > 5 || y_value < -5) && x_value <= 5 && x_value >= -5){
                        Left_bytes[1] = (byte) (posY & 0xFF);
                        Right_bytes[1] = (byte) (posY & 0xFF);
                        Left_bytes[1] |= forward_Mask; // turns the direction to forward
                        Right_bytes[1] |= forward_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 0,L=R=|Y|
                    else if(y_value < -5 && x_value <= 5 && x_value >= -5){
                        Left_bytes[1] = (byte) (posY & 0xFF);
                        Right_bytes[1] = (byte) (posY & 0xFF);
                        Left_bytes[1] &= reverse_Mask; // turns the direction to reverse
                        Right_bytes[1] &= reverse_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 1, 1/2R=L=X
                    else if(x_value > 5 && y_value <= 5 && y_value >= -5){
                        corrected = posX / 2;

                        Left_bytes[1] = (byte) (posX & 0xFF);
                        Right_bytes[1] = (byte) (corrected & 0xFF);
                        Left_bytes[1] |= forward_Mask; // turns the direction to forward
                        Right_bytes[1] |= forward_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 1, 1/2L=R=|X|
                    else if(x_value < -5 && y_value <= 5 && y_value >= -5){
                        corrected = posX / 2;

                        Left_bytes[1] = (byte) (corrected & 0xFF);
                        Right_bytes[1] = (byte) (posX & 0xFF);
                        Left_bytes[1] |= forward_Mask; // turns the direction to forward
                        Right_bytes[1] |= forward_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 1, 3/4R=L=X
                    else if(y_value > 0 && x_value > 0 && (posX > posY || posX == posY)){
                        corrected = (3 * posX) / 4;

                        Left_bytes[1] = (byte) (posX & 0xFF);
                        Right_bytes[1] = (byte) (corrected & 0xFF);
                        Left_bytes[1] |= forward_Mask; // turns the direction to forward
                        Right_bytes[1] |= forward_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 1, 3/4R=L=Y
                    else if(y_value > 0 && x_value > 0 && posX < posY){
                        corrected = (3 * posY) / 4;

                        Left_bytes[1] = (byte) (posY & 0xFF);
                        Right_bytes[1] = (byte) (corrected & 0xFF);
                        Left_bytes[1] |= forward_Mask; // turns the direction to forward
                        Right_bytes[1] |= forward_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 1, 3/4L=R=|X|
                    else if(y_value > 0 && x_value < 0 && (posX > posY || posX == posY)){
                        corrected = (3 * posX) / 4;

                        Left_bytes[1] = (byte) (corrected & 0xFF);
                        Right_bytes[1] = (byte) (posX & 0xFF);
                        Left_bytes[1] |= forward_Mask; // turns the direction to forward
                        Right_bytes[1] |= forward_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 1, 3/4L=R=Y
                    else if(y_value > 0 && x_value < 0 && posX < posY){
                        corrected = (3 * posY) / 4;

                        Left_bytes[1] = (byte) (corrected & 0xFF);
                        Right_bytes[1] = (byte) (posY & 0xFF);
                        Left_bytes[1] |= forward_Mask; // turns the direction to forward
                        Right_bytes[1] |= forward_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 0, 3/4R=L=X
                    else if(y_value < 0 && x_value > 0 && (posX > posY || posX == posY)){
                        corrected = (3 * posX) / 4;

                        Left_bytes[1] = (byte) (posX & 0xFF);
                        Right_bytes[1] = (byte) (corrected & 0xFF);
                        Left_bytes[1] &= reverse_Mask; // turns the direction to reverse
                        Right_bytes[1] &= reverse_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 0, 3/4R=L=|Y|
                    else if(y_value < 0 && x_value > 0 && posX < posY){
                        corrected = (3 * posY) / 4;

                        Left_bytes[1] = (byte) (posY & 0xFF);
                        Right_bytes[1] = (byte) (corrected & 0xFF);
                        Left_bytes[1] &= reverse_Mask; // turns the direction to reverse
                        Right_bytes[1] &= reverse_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 0, 3/4L=R=|X|
                    else if(y_value < 0 && x_value < 0 && (posX > posY || posX == posY)){
                        corrected = (3 * posX) / 4;

                        Left_bytes[1] = (byte) (corrected & 0xFF);
                        Right_bytes[1] = (byte) (posX & 0xFF);
                        Left_bytes[1] &= reverse_Mask; // turns the direction to reverse
                        Right_bytes[1] &= reverse_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Direction = 0, 3/4L=R=|Y|
                    else if(y_value < 0 && x_value < 0 && posX < posY){
                        corrected = (3 * posY) / 4;

                        Left_bytes[1] = (byte) (corrected & 0xFF);
                        Right_bytes[1] = (byte) (posY & 0xFF);
                        Left_bytes[1] &= reverse_Mask; // turns the direction to reverse
                        Right_bytes[1] &= reverse_Mask;
                        left_characteristic.setValue(Left_bytes);
                        right_characteristic.setValue(Right_bytes);
                    }

                    // Default to off
                    else{
                        left_characteristic.setValue(new byte[]{0x00});
                        right_characteristic.setValue(new byte[]{0x00});
                    }
                    mConnectedGatt.writeCharacteristic(left_characteristic);
                    mConnectedGatt.writeCharacteristic(right_characteristic);
                }});
        }

    }

}