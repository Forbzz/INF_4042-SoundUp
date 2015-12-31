package org.esiea.dondin_ta.soundup.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.esiea.dondin_ta.soundup.R;


public class InstructionActivity extends Activity {

    private Button returnBtn;

    private ActionBar bar;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruction);

        bar = getActionBar();

        returnBtn = (Button) findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstructionActivity.this,MainActivity.class));
            }
        });

    }

}