package com.brigid.android.mynextcareer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ErrorScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_screen);
    }

    public void onClickRetake(View v) // we launch the hollandTest activity. there's no need to restart, it's done on onFinishClick() there.
    {
        Intent myIntent = new Intent(this, HollandTest.class);
        startActivity(myIntent);
    }
}
