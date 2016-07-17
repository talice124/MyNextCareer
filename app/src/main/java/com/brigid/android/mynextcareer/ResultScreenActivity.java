package com.brigid.android.mynextcareer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ResultScreenActivity extends AppCompatActivity {

    private String hollandRes;
    private String explainRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        TextView hollandid = (TextView) findViewById(R.id.hollandid);
        TextView explain = (TextView) findViewById(R.id.explanation);

        Bundle resultScreenData = getIntent().getExtras();
        if (resultScreenData == null) {
            return;
        }

        hollandRes = resultScreenData.getString("hstr");
        hollandid.setText(hollandRes);

        explainRes = resultScreenData.getString("details");
        explain.setText(explainRes);

    }

    public void OpenListURL(View v)
    {
        String listURL;

        // this is a check to see which link to put. if there's an alternative than it's not the default hollandRes
        Bundle resultScreenData = getIntent().getExtras();
        if (resultScreenData == null)
            return;
        String alternative = resultScreenData.getString("alternative");
        if (alternative == null)  // default. meaning - the result has an online entry
        {
            listURL = "http://www.chroniclecareerlibrary.com/CGP/CGP/LISTSHOC/" + hollandRes + ".HTML";
        }
        else if (alternative.equals("")) // there is no good alternative, so we give the link to the list of codes
        {
            listURL = "http://www.chroniclecareerlibrary.com/CGP/CGP/2ndpghoc.html";
        }
        else // there is a good alternative, use it
        {
            listURL = "http://www.chroniclecareerlibrary.com/CGP/CGP/LISTSHOC/" + alternative + ".HTML";
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(listURL));
        startActivity(browserIntent);
    }
}
