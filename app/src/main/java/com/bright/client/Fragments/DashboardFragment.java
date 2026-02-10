package com.bright.client.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bright.client.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private BarChart salesBarChart;

    private LineChart lineChart;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initViews(view);
        salesGraph();


        return view;
    }

    private void initViews(View view) {
        lineChart = view.findViewById(R.id.lineChart);
    }

    private void salesGraph() {

// Sample sales data
        ArrayList<Entry> sales = new ArrayList<>();
        sales.add(new Entry(0, 600));
        sales.add(new Entry(1, 15000));
        sales.add(new Entry(2, 4000));
        sales.add(new Entry(3, 18000));
        sales.add(new Entry(4, 12000));

// Line dataset
        LineDataSet dataSet = new LineDataSet(sales, "Sales Trend");
        dataSet.setColor(Color.parseColor("#DEF6D0"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(Color.parseColor("#4CAF50"));
        dataSet.setDrawValues(false);      // clean look
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // smooth line

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May"};

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        lineChart.animateX(800);
        lineChart.invalidate();


    }
}