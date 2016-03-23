package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.HistoricalQuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.ArrayList;
import java.util.List;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private LineChartView mChart;
    int mMinAxis;
    int mMaxAxis;
    Context mContext;
    private TextView mLinechartEmpty;

    private android.support.v4.app.Fragment mCurrFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        mContext = getApplicationContext();
        mChart = (LineChartView) findViewById(R.id.linechart);
        mLinechartEmpty = (TextView) findViewById(R.id.linechart_empty);
        getLoaderManager().initLoader(1, null, this);
    }


    @Nullable
    private String getSymbol() {
        Intent intent = getIntent();
        String symbol = null;
        if (intent != null && intent.hasExtra(getString(R.string.intent_symbol)))
            symbol = intent.getStringExtra(getString(R.string.intent_symbol));
        return symbol;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){

        return new CursorLoader(this,
                QuoteProvider.HistoricalQuotes.CONTENT_URI,
                new String[]{HistoricalQuoteColumns.CLOSE_DATE, HistoricalQuoteColumns.CLOSE},
                HistoricalQuoteColumns.SYMBOL + "= ?",
                new String[]{getSymbol()},
                HistoricalQuoteColumns.CLOSE_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){

        if(cursor.getCount() != 0 )
            drawLineChart(fillDataSet(cursor), cursor.getCount());
        else
            updateEmptyView(cursor);
    }

    private LineSet fillDataSet(Cursor cursor) {

        int count = cursor.getCount();
        List<String> dates = new ArrayList<String>();
        float[] quotes = new float[count];
        cursor.moveToFirst();
        int dateColumnIndex = cursor.getColumnIndex(HistoricalQuoteColumns.CLOSE_DATE);
        int quoteColumnIndex = cursor.getColumnIndex(HistoricalQuoteColumns.CLOSE);
        for (int i = 0; i < count; i++) {
            if(i==0+5 || i==count-5)
                dates.add(cursor.getString(dateColumnIndex));
            else
                dates.add("");
            quotes[i] = cursor.getFloat(quoteColumnIndex);
            cursor.moveToNext();
        }
        mMinAxis = (int) getMin(quotes)-5;
        mMaxAxis = (int) getMax(quotes)+5;
        String[] datesArray = new String[dates.size()];
        datesArray = dates.toArray(datesArray);

        return new LineSet(datesArray, quotes);
    }


    private void drawLineChart(LineSet dataset, int count) {

        mLinechartEmpty.setVisibility(View.GONE);
        dataset.setColor(Color.parseColor("#00cc00"))
                .setThickness(Tools.fromDpToPx(3))
                .setSmooth(true)
                .beginAt(1).endAt(count);
        mChart.addData(dataset);
        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#ffffff"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        mChart.setBorderSpacing(Tools.fromDpToPx(30))
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#ff4a00"))
                .setFontSize(60)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXAxis(true)
                .setYAxis(true)
                .setGrid(ChartView.GridType.FULL, 1, 5, gridPaint)
                .setAxisBorderValues(mMinAxis, mMaxAxis );  // 110, count

        mChart.show();
    }

    private void updateEmptyView(Cursor cursor) {
        if(cursor.getCount() == 0) {
            if (null != mLinechartEmpty){
                int message = R.string.no_historical_data_available;
                if(!Utils.isNetworkAvailable(mContext)) {
                    message = R.string.no_network;
                }
                mLinechartEmpty.setText(message);
            }
        } else {
            mLinechartEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){

    }

    public static float getMax(float[] array) {
        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];

            }
        }
        return max;
    }

    public static float getMin(float[] array) {
        float min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];

            }
        }
        return min;
    }


}
