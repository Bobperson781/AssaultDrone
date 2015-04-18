package com.example.teamone.assaultdrone;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class BeastMode extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beast_mode);

        final TextView x=(TextView)findViewById(R.id.x);
        final TextView y=(TextView)findViewById(R.id.y);

        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.beast);
        //Thread analog = new Thread(new Runnable(){

        //public void run() {
        rl.addView(new com.example.teamone.assaultdrone.AnalogStick(this, x, y));
        //Button up =
         //}
        //});
    }


    // Should send a 1 until otherwise toggled
    public void LaserOnToggle(View v){
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.Laser:

                break;
            default:
                break;
        }
    }

    // Should send a boolean bit for firing
    public void FireOnClick(View v){
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.Fire:

                break;
            default:
                break;
        }
    }

    // Should send Direction int = 1000 (Binary)
    public void UpOnClick(View v){
        ImageButton button = (ImageButton) v;
        switch (button.getId()) {
            case R.id.Up:

                break;
            default:
                break;
        }
    }

    // Should send Direction int = 0100 (Binary)
    public void DownOnClick(View v){
        ImageButton button = (ImageButton) v;
        switch (button.getId()) {
            case R.id.Down:

                break;
            default:
                break;
        }
    }

    // Should send Direction int = 0010 (Binary)
    public void LeftOnClick(View v){
        ImageButton button = (ImageButton) v;
        switch (button.getId()) {
            case R.id.Left:

                break;
            default:
                break;
        }
    }

    // Should send Direction int = 0001 (Binary)
    public void RightOnClick(View v){
        ImageButton button = (ImageButton) v;
        switch (button.getId()) {
            case R.id.Right:

                break;
            default:
                break;
        }
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
}