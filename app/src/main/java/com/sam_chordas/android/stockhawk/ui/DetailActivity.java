package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sam_chordas.android.stockhawk.R;


public class DetailActivity extends AppCompatActivity {


    private android.support.v4.app.Fragment mCurrFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrFragment = new LineChartFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, mCurrFragment)
                .commit();


        //  Animation anim = new Animation().setStartPoint(0, .5f).setEndAction(action);


       // mChart.show(anim);

    }






}
