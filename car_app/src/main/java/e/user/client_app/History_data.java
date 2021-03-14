package e.user.client_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.internal.view.SupportMenu;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class History_data extends AppCompatActivity {
    String car_num;
    Handler handler = new Handler();
    AlertDialog mAlertDialog;
    double[][] myArr = ((double[][]) Array.newInstance(double.class, new int[]{4, 100}));
    ProgressDialogUtil progressBar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.layout_history_data);
        this.car_num = getIntent().getStringExtra("car_num");
        ((TextView) findViewById(R.id.car_num)).setText(this.car_num);
        ((Button) findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(History_data.this, MainActivity.class);
                History_data.this.startActivity(intent);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        SocketHandler socket = new SocketHandler();
        socket.setReq(2);
        socket.setNumber(this.car_num);
        socket.con();
        this.myArr = socket.getData();
        System.out.println(this.myArr[1]);
        for (int i = 0; i < 4; i++) {
            line_chart(i);
        }
        ((Button) findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        int progressStatus = 0;
                        while (progressStatus <= 250) {
                            progressStatus++;
                            History_data.this.handler.post(new Runnable() {
                                public void run() {
                                    History_data.this.mAlertDialog.show();
                                }
                            });
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                        Intent intent = History_data.this.getIntent();
                        History_data.this.finish();
                        History_data.this.startActivity(intent);
                    }
                }).start();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mAlertDialog = new AlertDialog.Builder(this, R.style.CustomProgressDialog).create();
        View loadView = LayoutInflater.from(this).inflate(R.layout.progress_view, (ViewGroup) null);
        this.mAlertDialog.setView(loadView, 0, 0, 0, 0);
        this.mAlertDialog.setCanceledOnTouchOutside(false);
        ((TextView) loadView.findViewById(R.id.tvTip)).setText("運算中...");
    }

    public void line_chart(int index) {
        float[] array = new float[100];
        float min = 1000000.0f;
        float max = 0.0f;
        for (int i = 0; i < 100; i++) {
            array[i] = (float) this.myArr[index][i];
            if (array[i] > max) {
                max = array[i];
            }
            if (array[i] < min) {
                min = array[i];
            }
        }
        ArrayList<Entry> poitList = new ArrayList<>();
        for (int i2 = 0; i2 < 100; i2++) {
            poitList.add(new Entry((float) i2, array[i2]));
        }
        LineDataSet dataSet = new LineDataSet(poitList, "");
        dataSet.setHighLightColor(SupportMenu.CATEGORY_MASK);
        dataSet.setDrawValues(false);
        if (index == 0) {
            LineData data = new LineData(dataSet);
            LineChart chart = (LineChart) findViewById(R.id.chart);
            chart.setData(data);
            YAxis leftAxis = chart.getAxisLeft();
            chart.getAxisRight().setEnabled(false);
            leftAxis.setAxisMinimum(min);
            leftAxis.setAxisMaximum(max);
            leftAxis.setDrawGridLines(true);
        } else if (index == 1) {
            LineData data2 = new LineData(dataSet);
            LineChart chart2 = (LineChart) findViewById(R.id.chart1);
            chart2.setData(data2);
            YAxis leftAxis2 = chart2.getAxisLeft();
            chart2.getAxisRight().setEnabled(false);
            leftAxis2.setAxisMinimum(min);
            leftAxis2.setAxisMaximum(max);
            leftAxis2.setDrawGridLines(true);
        } else if (index == 2) {
            LineData data3 = new LineData(dataSet);
            LineChart chart3 = (LineChart) findViewById(R.id.chart2);
            chart3.setData(data3);
            YAxis leftAxis3 = chart3.getAxisLeft();
            chart3.getAxisRight().setEnabled(false);
            leftAxis3.setAxisMinimum(min);
            leftAxis3.setAxisMaximum(max);
            leftAxis3.setDrawGridLines(true);
        } else if (index == 3) {
            LineData data4 = new LineData(dataSet);
            LineChart chart4 = (LineChart) findViewById(R.id.chart3);
            chart4.setData(data4);
            YAxis leftAxis4 = chart4.getAxisLeft();
            chart4.getAxisRight().setEnabled(false);
            leftAxis4.setAxisMinimum(min);
            leftAxis4.setAxisMaximum(max);
            leftAxis4.setDrawGridLines(true);
        }
    }
}
