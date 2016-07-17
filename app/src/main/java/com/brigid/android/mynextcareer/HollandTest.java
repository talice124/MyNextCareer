package com.brigid.android.mynextcareer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.String;
import java.util.Hashtable;


public class HollandTest extends AppCompatActivity {
    private RadioGroup usersAnswersRadioG;
    private static int questionNumber = 0;
    protected String[] questions;
    private static String HollandString = "";
    private static Hashtable<String,String> htable = new Hashtable<String,String>();
    private enum HollandType {
        Realistic(0), Investigative(1), Artistic(2), Social(3), Enterprising(4), Conventional(5), HollandError(6);
        private final int value;

        HollandType(int value) {
            this.value = value;
            this.intToHolland(value);
        }

        public int getValue() {
            return value;
        }

        public static HollandType intToHolland(int i)
        {
            if ((i<0)||(i>5)) // illegal input
            {
                return HollandError;
            }
            switch (i)
            {
                case 0: return Realistic;
                case 1: return Investigative;
                case 2: return Artistic;
                case 3: return Social;
                case 4: return Enterprising;
                case 5: return Conventional;
                default: // illegal input. plus, since we checked - code run is never supposed to get here
                    return HollandError;
            }
        }
    };
    private Button next_button, finish_button;
    private static int[] TotalHollandScore = new int[] {0, 0, 0, 0, 0, 0};
    public String getHollandString()
    {
        return HollandString;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holland_test);

        questions = getResources().getStringArray(R.array.questions);

        usersAnswersRadioG = (RadioGroup) findViewById(R.id.AnswerGroup);

        finish_button = (Button) findViewById(R.id.finishbutton);
        finish_button.setVisibility(View.GONE); // we hide the finish button and show it only when we finish...
        next_button = (Button)findViewById(R.id.nextButton);
        showQuestion();
}


    public void onClickNext(View v) {

        boolean added = addToHollandScore(v);
        if (!added) // this means the user chose an invalid answer,
                    // so he needs to chose and press next again. we don't show next q yet
            return;
        showNextQuestion();
        usersAnswersRadioG.clearCheck(); // next q - clear radio buttons
    }

    public boolean addToHollandScore(View v) // adds to the holland array by chosen button.
    // if no answer is chosen, puts out an error for the user and returns false
    // the score is 4-0, and it is added to the place in the array that corresponds to
    // the holland type of this question. so all R questions add to Score of R est.
    {
        int selectedId = usersAnswersRadioG.getCheckedRadioButtonId();
        if (selectedId == -1) // no answer selected
        {
            Toast.makeText(getApplicationContext(), "Please chose a valid answer to proceed",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        int hollandPlace = getHollandPlace();
        // find which radioButton is checked by id
        switch (selectedId) {
            case R.id.RB1:  TotalHollandScore[hollandPlace] += 4;
                break;
            case R.id.RB2:  TotalHollandScore[hollandPlace] += 3;
                break;
            case R.id.RB3:  TotalHollandScore[hollandPlace] += 2;
                break;
            case R.id.RB4:  TotalHollandScore[hollandPlace] += 1;
                break;
            case R.id.RB5:  TotalHollandScore[hollandPlace] += 0;
                break;
            default: Toast.makeText(getApplicationContext(), "Please chose a valid answer to proceed",
                    Toast.LENGTH_SHORT).show();
                return false; // this is probably a user error so we return and he needs to answer this again, not showing next question
        }

        return true;

    }


    public void onClickFinish(View v) {

        boolean added = addToHollandScore(v);
        if (!added)
            return;

        Intent myIntent;
        String details = "";
        String result;

        String[] significance = {"Dominant", "Secondary", "Flavor"};

        int mymax[] = CalcHolland();
        initHollandNonOccupationsHash();

        for (int i=0; i<3; i++) {
            HollandType hType = HollandType.intToHolland(mymax[i]);
            String str = hType.toString();
            HollandString += str.charAt(0);
            details += str.charAt(0) + " - " + str + "  (" + significance[i] + " Quality)\n";
        }

        result = "Meaning of the letters, highest score first:\n" + details + "\n";

        // Test 2 cases:
        // if the holland string is valid, go to result screen
        // if not valid (the user put all most negative. not likely but possible) go to error screen

        if ((mymax[0] == 6) || (mymax[1] == 6) || (mymax[2] == 6)) // the holland result contains error letter, go to error
        {
            myIntent = new Intent(this, ErrorScreenActivity.class);
        }
        else
        {
            myIntent = new Intent(this, ResultScreenActivity.class);

            if (htable.containsKey(HollandString)) {
                // if we got here, the holland string does not have an entry. so:
                String alternative = htable.get(HollandString);
                if (alternative.equals("")) // this case means that the alternative sucks. so we give the user a free hand to find his career
                {
                    result += "\n" + "However, no exact match is found in the Holland code for this combination, so you are free to look at similar combinations on this link:";
                } else {
                    result += "\n" + "No exact match found for " + HollandString + " in the Holland code occupation list. Showing results for " + alternative + "\n";
                }
                myIntent.putExtra("alternative", alternative);
            }
            // holland string is valid, add the result and details to send to the result screen
            // add the holland string result to the intent so we can use it on the result screen
            myIntent.putExtra("hstr", HollandString);
            myIntent.putExtra("details", result);
        }

        // since this is finish, we initialize everything here, so we won't have to if there's a retake.
        // this means: no returns!
        // since we initialize the HollandString as well, this is done only after it's passed.
        initialize();


        //Fire that second activity - result screen
        startActivity(myIntent);

    }

    public void initialize()
    {
        usersAnswersRadioG.clearCheck();
        questionNumber = 0;
        HollandString = "";
        for (int i = 0; i < 6; i++) {
                TotalHollandScore[i] = 0;
        }
        finish_button.setVisibility(View.GONE);
        next_button.setVisibility(View.VISIBLE);
    }


    public int[] CalcHolland()
    {
        int ind_max[] = {6, 6, 6};
        int max[] = {0, 0, 0};

        // find first max
        for (int i=0; i<6; i++)
        {
            if (max[0] < TotalHollandScore[i])
            {
                // found a new max! all move to one less significant, last is forgotten

                max[2] = max[1];
                ind_max[2] = ind_max[1];

                max[1] = max[0];
                ind_max[1] = ind_max[0];

                max[0] = TotalHollandScore[i];
                ind_max[0] = i;
            }
            else if (max[1] < TotalHollandScore[i])
            {
                // found a new middle! switch accordingly
                max[2] = max[1];
                ind_max[2] = ind_max[1];

                max[1] = TotalHollandScore[i];
                ind_max[1] = i;
            }
            else if (max[2] < TotalHollandScore[i])
            {
                // found a new least significant letter. swap the last only
                max[2] = TotalHollandScore[i];
                ind_max[2] = i;
            }
        }

        return ind_max;
    }

    private int getHollandPlace()
    {
        // the questions are sorted so that one element of each type comes every cycle
        // Realistic(0), Investigative(1), Artistic(2), Social(3), Enterprising(4), Conventional(5)
        // q1 - Realistic, q2 - Investigative, ... q6 - Realistic again and so on
        // the math is just %, simple, however in case we change the data structure in future
        // it will all be changed here in one place
        return (questionNumber % 6);
    }

    private void showNextQuestion()
    // this function takes care of which question number it is and if there's something special to do about it
    {
        questionNumber++;
        if (questionNumber>47)
            return;
        if (questionNumber == 47) // this is the last question. hide the next button and show finish
        {
            next_button.setVisibility(View.GONE);
            finish_button.setVisibility(View.VISIBLE);
        }

        TextView txt = (TextView) findViewById(R.id.questionHeaderText);
        if (questionNumber == 17) // this question is the 9 to 5
        {
            txt.setText("Would you enjoy:");
        }
        if (questionNumber == 18) // the 9 to 5 question was last question so go back to normal
        {
            txt.setText(R.string.header);
        }
        showQuestion();
    }

    private void showQuestion() // everything is checked and increased in showNextQuestion. if we got here we just show this question.
    {
        TextView txt = (TextView) findViewById(R.id.Question);
        String fullQuestion = (questionNumber + 1) + ". " + questions[questionNumber];
        txt.setText(fullQuestion);
    }


    private void initHollandNonOccupationsHash()
    // most combinations have a suiting holland list of occupations. however, some don't.
    // so this function maps all the combinations that don't have a match and if there's an easy
    // switch and closest other combination, matches it to it. otherwise, keeps an empty string
    {
        htable.put("ACE","AEC");
        htable.put("ACI","");
        htable.put("ACR","");
        htable.put("AIC","");
        htable.put("ARC","");

        htable.put("CAE","");
        htable.put("CAI","CIA");
        htable.put("CAR","CRA");
        htable.put("CAS","");
        htable.put("CEA","");
        htable.put("CSA","");

        htable.put("EAI","EIA");
        htable.put("EAR","ERA");
        htable.put("ECA","EAC");

        htable.put("IAC","");
        htable.put("ICA","");
        htable.put("ICE","IEC");

        htable.put("RAC","RCA");
        htable.put("RAI","RIA");
        htable.put("RAS","");
        htable.put("RSA","");

        htable.put("SAR","");
        htable.put("SIC","SCI");
        htable.put("SRA","");

    }

}


