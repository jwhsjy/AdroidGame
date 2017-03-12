package kr.co.topcredu.chartapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity  {

    public static String LOG_TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //파이 그래프
       // Button btnPieChart =  (Button)findViewById(R.id.btnPieChart);
       // btnPieChart.setOnClickListener(this);


        //라인 그래프
        //Button btnLineChart =  (Button)findViewById(R.id.btnLineChart);
        //btnLineChart.setOnClickListener(this);


        //막대 그래프
        //Button btnBarChart =  (Button)findViewById(R.id.btnBarChart);
       // btnBarChart.setOnClickListener(this);
    }

    public void ChartClick(View v) {
        switch (v.getId()) {
            //파이그래프(PIE Chart)
            case R.id.btnPieChart:
                Toast.makeText(MainActivity.this, "Pie Chart", Toast.LENGTH_LONG);
                Log.i(LOG_TAG, "PIE Chart Start...");
                startActivity(new Intent(this, PieChartActivity.class));
                break;

            //라인 그래프(Line Chart)
            case R.id.btnLineChart:
                Toast.makeText(MainActivity.this, "Line Chart", Toast.LENGTH_LONG);
                Log.i(LOG_TAG, "Line Chart Start...");
                startActivity(new Intent(this, LineChartActivity.class));
                break;

            //막대 그래프(Bar Chart)
            case R.id.btnBarChart:
                Toast.makeText(MainActivity.this, "Bar Chart", Toast.LENGTH_LONG);
                Log.i(LOG_TAG, "Bar Chart Start...");
                startActivity(new Intent(this, BarChartActivity.class));
                break;
        }

    }
}
