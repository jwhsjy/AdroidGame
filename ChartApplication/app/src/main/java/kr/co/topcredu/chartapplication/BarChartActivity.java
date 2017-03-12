package kr.co.topcredu.chartapplication;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class BarChartActivity extends AppCompatActivity {

    private GraphicalView mChartView;
    private String[] mWeek = new String[]{"1주","2주","3주","4주"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        drawChart();
    }

    private void drawChart() {

        int[] x = {0, 1, 2, 3};

        int[] food = {1000, 2000, 1000, 0};

        int[] shoping = {1000, 2000, 3000, 0};

        int [] traffic = {500, 2000, 1000, 0};



        XYSeries foodSeries = new XYSeries("식비");
        XYSeries shopingSeries = new XYSeries("쇼핑비");
        XYSeries trafficSeries = new XYSeries("교통비");

        // Adding data to Income and Expense Series

        for (int i = 0; i < x.length; i++) {

            foodSeries.add(i, food[i]);
            shopingSeries.add(i, shoping[i]);
            trafficSeries.add(i, traffic[i]);

        }


        // Creating a dataset to hold each series

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();


        dataset.addSeries(foodSeries);
        dataset.addSeries(shopingSeries);
        dataset.addSeries(trafficSeries);


        XYSeriesRenderer foodRenderer = new XYSeriesRenderer();

        foodRenderer.setColor(Color.parseColor("#4641D9")); //color of the graph set to cyan
        foodRenderer.setFillPoints(true);
        foodRenderer.setLineWidth(100);
        foodRenderer.setDisplayChartValues(true);
        foodRenderer.setDisplayChartValuesDistance(10); //setting chart value distance

        XYSeriesRenderer shopRenderer = new XYSeriesRenderer();
        shopRenderer.setColor(Color.parseColor("#6B9900"));
        shopRenderer.setFillPoints(true);
        shopRenderer.setLineWidth(100);
        shopRenderer.setDisplayChartValues(true);


        XYSeriesRenderer trafficRenderer = new XYSeriesRenderer();
        trafficRenderer.setColor(Color.parseColor("#980000"));
        trafficRenderer.setFillPoints(true);
        trafficRenderer.setLineWidth(100);
        trafficRenderer.setDisplayChartValues(true);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

        multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);

        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("주간소비내역");
        multiRenderer.setXTitle("Year 2017");
        multiRenderer.setYTitle("Amount in 천원");


        multiRenderer.setChartTitleTextSize(30);
        multiRenderer.setAxisTitleTextSize(10);
        //setting text size of the graph lable
        multiRenderer.setLabelsTextSize(10);
        //setting zoom buttons visiblity

        multiRenderer.setZoomButtonsVisible(false);
        //setting pan enablity which uses graph to move on both axis
        multiRenderer.setPanEnabled(false, false);
        //setting click false on graph
        multiRenderer.setClickEnabled(false);

        //setting zoom to false on both axis

        multiRenderer.setZoomEnabled(false, false);

        //setting lines to display on y axis

        multiRenderer.setShowGridY(false);

        //setting lines to display on x axis

        multiRenderer.setShowGridX(false);

        //setting legend to fit the screen size

        multiRenderer.setFitLegend(true);

        //setting displaying line on grid

        multiRenderer.setShowGrid(false);

        //setting zoom to false

        multiRenderer.setZoomEnabled(false);

        //setting external zoom functions to false

        multiRenderer.setExternalZoomEnabled(false);

        //setting displaying lines on graph to be formatted(like using graphics)

        multiRenderer.setAntialiasing(true);

        //setting to in scroll to false

        multiRenderer.setInScroll(false);

        //setting to set legend height of the graph

        multiRenderer.setLegendHeight(30);

        //setting x axis label align

        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);

        //setting y axis label to align

        multiRenderer.setYLabelsAlign(Paint.Align.LEFT);

        //setting text style

        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);

        //setting no of values to display in y axis

        multiRenderer.setYLabels(10);

        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.

        // if you use dynamic values then get the max y value and set here

        multiRenderer.setYAxisMax(4000);

        //setting used to move the graph on xaxiz to .5 to the right

        multiRenderer.setXAxisMin(-0.5);

        //setting max values to be display in x axis

        multiRenderer.setXAxisMax(11);

        //setting bar size or space between two bars

        multiRenderer.setBarSpacing(0.5);

        //Setting background color of the graph to transparent

        multiRenderer.setBackgroundColor(Color.TRANSPARENT);

        //Setting margin color of the graph to transparent

        multiRenderer.setMarginsColor(Color.parseColor("#353535"));

        multiRenderer.setApplyBackgroundColor(true);

        //setting the margin size for the graph in the order top, left, bottom, right

        multiRenderer.setMargins(new int[]{60, 60, 60, 60});

        for (int i = 0; i < x.length; i++) {

            multiRenderer.addXTextLabel(i, mWeek[i]);

        }

        // Adding incomeRenderer and expenseRenderer to multipleRenderer

        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer

        // should be same

        multiRenderer.addSeriesRenderer(foodRenderer);
        multiRenderer.addSeriesRenderer(shopRenderer);
        multiRenderer.addSeriesRenderer(trafficRenderer);

        //this part is used to display graph on the xml

        LinearLayout layout = (LinearLayout) findViewById(R.id.chart_bar);

        //remove any views before u paint the chart

        layout.removeAllViews();

        //drawing bar chart

        mChartView = ChartFactory.getBarChartView(this, dataset, multiRenderer, BarChart.Type.DEFAULT);


        layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,

                LinearLayout.LayoutParams.FILL_PARENT));

    }

}

