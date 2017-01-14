package com.frkn.simsek;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

/**
 * Created by frkn on 08.01.2017.
 */

public class SearchCity extends AppCompatActivity {

    String[] autoCompleteString = {
        "AKRON/ABD",
        "ALBANY/ABD",
        "ALBUQUERQUE/ABD",
        "ALEXANDRIA/ABD",
        "ALLENTOWN/ABD",
        "AMES/ABD",
        "AMHERST/ABD",
        "ANAHEIM/ABD",
        "ANCHORAGE/ABD",
        "ANDERSON/ABD",
        "ANN-ARBOR/ABD",
        "ANNAPOLIS/ABD",
        "ARNOLD/ABD",
        "ARUBA/ABD",
        "ATLANTA/ABD",
        "AUBURN/ABD",
        "AUGUSTA/ABD",
        "AUSTIN/ABD",
        "BALTIMORE/ABD",
        "BANNER-ELK/ABD",
        "BATON-ROUGE/ABD",
        "BAY-CITY/ABD",
        "BEAUMONT/ABD",
        "BENNINGTON/ABD",
        "BENTON/ABD",
        "BERKELEY/ABD",
        "BETHESDA/ABD",
        "BETHLEHEN/ABD",
        "BINGHAMTON/ABD",
        "BIRMINGHAM/ABD"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto_complete_textview);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, autoCompleteString);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);

    }

}
