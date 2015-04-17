package com.example.teamone.assaultdrone;

//import com.example.teamone.assaultdrone.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class main_menu extends Activity implements View.OnClickListener {

    Button playButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);
        //getActionBar().hide();
    }

    // will this save?
    public void onClick(View v) {
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.button_play:
                playButtonClick();
                break;
            default:
                break;
        }
    }

    private void playButtonClick() {
        Intent i = new Intent(this, BeastMode.class);
        startActivity(i);
    }
}