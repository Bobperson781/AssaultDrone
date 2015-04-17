package com.example.teamone.assaultdrone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class BeastMode extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beast_mode);

        TextView x=(TextView)findViewById(R.id.x);
        TextView y=(TextView)findViewById(R.id.y);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.beast);
        rl.addView(new com.example.teamone.assaultdrone.AnalogStick(this,x,y));
        //Button up =
    }

    // Should send a 1 until otherwise toggled
    public void LaserOnToggle(View v){
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.button_play:

                break;
            default:
                break;
        }
    }

    // Should send a boolean bit for firing
    public void FireOnClick(View v){
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.button_play:
                Intent i = new Intent(this, main_menu.class);
                startActivity(i);// does this save?
                break;
            default:
                break;
        }
    }

    // Should send Direction int = 1000 (Binary)
    public void UpOnClick(View v){
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.button_play:

                break;
            default:
                break;
        }
    }

    // Should send Direction int = 0100 (Binary)
    public void DownOnClick(View v){
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.button_play:

                break;
            default:
                break;
        }
    }

    // Should send Direction int = 0010 (Binary)
    public void LeftOnClick(View v){
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.button_play:

                break;
            default:
                break;
        }
    }

    // Should send Direction int = 0001 (Binary)
    public void RightOnClick(View v){
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.button_play:

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