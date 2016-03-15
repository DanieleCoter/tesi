package it.unibg.p3d4amb.carracingcarboard.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.unibg.p3d4amb.carracingcarboard.R;

public class SelectActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent startSelect = new Intent(SelectActivity.this, LogInActivity.class);
                startActivity(startSelect);
            }
        });
        Button button2 =(Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startSelect2 = new Intent(SelectActivity.this, LogIn2Activity.class);
                startActivity(startSelect2);
            }
        });
    }
}
