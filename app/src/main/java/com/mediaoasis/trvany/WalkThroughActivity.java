package com.mediaoasis.trvany;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mediaoasis.trvany.activities.user.UserAccessActivity;
import com.mediaoasis.trvany.adapters.ImageAdapter;
import com.mediaoasis.trvany.view.viewflow.CircleFlowIndicator;
import com.mediaoasis.trvany.view.viewflow.ViewFlow;


public class WalkThroughActivity extends AppCompatActivity {

    Button button;
    private ViewFlow viewFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        viewFlow = (ViewFlow) findViewById(R.id.viewflow);
        viewFlow.setAdapter(new ImageAdapter(WalkThroughActivity.this), 3);

        CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
        viewFlow.setFlowIndicator(indic);

        button = (Button) findViewById(R.id.buttonStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalkThroughActivity.this, UserAccessActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        viewFlow.onConfigurationChanged(newConfig);
    }
}
