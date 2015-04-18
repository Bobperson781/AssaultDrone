package com.example.teamone.assaultdrone;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


public class BeastMode extends Activity {

    private int panTilt = 0b0000;
    private int fire = 0b0;
    private int laser = 0b0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beast_mode);

        final TextView x=(TextView)findViewById(R.id.x);
        final TextView y=(TextView)findViewById(R.id.y);

        //TODO add multitouch sensing to the button detection?
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.beast);
        rl.addView(new com.example.teamone.assaultdrone.AnalogStick(this, x, y));

        final Button fireButton = (Button) findViewById(R.id.Fire);
        fireButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //fire button was pressed
                    fire = 0b1;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    fire = 0b0;
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
                    panTilt = 0b1000;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    panTilt = 0b0000;
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
                    panTilt = 0b0100;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    panTilt = 0b0000;
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
                    panTilt = 0b0010;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    panTilt = 0b0000;
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
                    panTilt = 0b0001;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    panTilt = 0b0000;
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
                    laser = 0b1;
                }else{
                    laser = 0b0;
                }
                break;
            default:
                break;
        }
    }

//    // Should send a boolean bit for firing
//    public void FireOnClick(View v){
//        Button button = (Button) v;
//        switch (button.getId()) {
//            case R.id.Fire:
//                    fire = 0b1;
//                break;
//            default:
//                break;
//        }
//    }

//    // Should send Direction int = 1000 (Binary)
//    public void UpOnClick(View v){
//        ImageButton button = (ImageButton) v;
//        switch (button.getId()) {
//            case R.id.Up:
//                    panTilt = 0b1000;
//                break;
//            default:
//                break;
//        }
//    }
//
//    // Should send Direction int = 0100 (Binary)
//    public void DownOnClick(View v){
//        ImageButton button = (ImageButton) v;
//        switch (button.getId()) {
//            case R.id.Down:
//                panTilt = 0b0100;
//                break;
//            default:
//                break;
//        }
//    }
//
//    // Should send Direction int = 0010 (Binary)
//    public void LeftOnClick(View v){
//        ImageButton button = (ImageButton) v;
//        switch (button.getId()) {
//            case R.id.Left:
//                    panTilt = 0b0010;
//                break;
//            default:
//                break;
//        }
//    }
//
//    // Should send Direction int = 0001 (Binary)
//    public void RightOnClick(View v){
//        ImageButton button = (ImageButton) v;
//        switch (button.getId()) {
//            case R.id.Right:
//                    panTilt = 0b0001;
//                break;
//            default:
//                break;
//        }
//    }

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
}