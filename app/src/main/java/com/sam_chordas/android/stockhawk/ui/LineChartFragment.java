package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.HistoricalQuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jon on 3/12/2016.
 */
public class LineChartFragment extends Fragment {

    private LineChartView mChart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.activity_line_graph, container, false);
        mChart = (LineChartView) layout.findViewById(R.id.linechart);

        Intent intent = getActivity().getIntent();
        String symbol = null;
        if (intent != null && intent.hasExtra("symbol"))
            symbol = intent.getStringExtra("symbol");
        TextView tv = (TextView) layout.findViewById(R.id.textView);
        tv.setText(symbol);


        // TODO:  Wrap in try/catch
        Cursor c = getContext().getContentResolver().query(
                QuoteProvider.HistoricalQuotes.CONTENT_URI,
                new String[]{HistoricalQuoteColumns.CLOSE_DATE, HistoricalQuoteColumns.CLOSE},
                HistoricalQuoteColumns.SYMBOL + "= ?",
                new String[]{symbol},
                HistoricalQuoteColumns.CLOSE_DATE + " ASC");

        int count = c.getCount();
        List<String> dates = new ArrayList<String>();
        float[] quotes = new float[count];
        // Put closing date labels into string array
        c.moveToFirst();
        int dateColumnIndex = c.getColumnIndex(HistoricalQuoteColumns.CLOSE_DATE);
        int quoteColumnIndex = c.getColumnIndex(HistoricalQuoteColumns.CLOSE);
        for (int i = 0; i < count; i++) {
            if(i==0+5 || i==count-5)
                dates.add(c.getString(dateColumnIndex));
            else
                dates.add("");
            quotes[i] = c.getFloat(quoteColumnIndex);
            c.moveToNext();
        }

        String[] datesArray = new String[dates.size()];
        datesArray = dates.toArray(datesArray);

        LineSet dataset = new LineSet(datesArray, quotes);

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
//                .setGrid(ChartView.GridType.VERTICAL, 1, 10, gridPaint)
                .setAxisBorderValues( ((int) getMin(quotes))-5 , ((int) getMax(quotes))+5 );  // 110, count


        mChart.show();

        if (c != null)
            c.close();

        return layout;
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
