package e.user.client_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {
    public static int situation = 0;
    Handler handler = new Handler();
    AlertDialog mAlertDialog;
    String number = "6860305399";
    ProgressDialogUtil progressBar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_check);
        if (situation == 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("輸入車牌");
            final EditText input = new EditText(this);
            input.setText("6860305399");
            input.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            alertDialog.setView(input);
            alertDialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.number = input.getText().toString();
                    dialog.cancel();
                }
            });
            alertDialog.show();
            situation = 1;
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        ((Button) findViewById(R.id.button10)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new CheckAsyncTask().execute(new Void[0]);
            }
        });
        ((Button) findViewById(R.id.button9)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        int progressStatus = 0;
                        while (progressStatus <= 250) {
                            progressStatus++;
                            MainActivity.this.handler.post(new Runnable() {
                                public void run() {
                                    MainActivity.this.mAlertDialog.show();
                                }
                            });
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, History_data.class);
                        intent.putExtra("car_num", MainActivity.this.number);
                        MainActivity.this.startActivity(intent);
                    }
                }).start();
            }
        });
        ((Button) findViewById(R.id.button11)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, setting.class);
                intent.putExtra("car_num", MainActivity.this.number);
                MainActivity.this.startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.situation = 0;
                Intent intent = MainActivity.this.getIntent();
                MainActivity.this.finish();
                MainActivity.this.startActivity(intent);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        this.mAlertDialog.dismiss();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mAlertDialog = new AlertDialog.Builder(this, R.style.CustomProgressDialog).create();
        View loadView = LayoutInflater.from(this).inflate(R.layout.progress_view, (ViewGroup) null);
        this.mAlertDialog.setView(loadView, 0, 0, 0, 0);
        this.mAlertDialog.setCanceledOnTouchOutside(false);
        ((TextView) loadView.findViewById(R.id.tvTip)).setText("載入中...");
    }

    public class CheckAsyncTask extends AsyncTask<Void, Void, Boolean> {
        String message;
        SocketHandler socket;

        public CheckAsyncTask() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            MainActivity.this.mAlertDialog.show();
            this.socket = new SocketHandler();
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voids) {
            try {
                this.socket.setReq(3);
                this.socket.setNumber(MainActivity.this.number);
                this.socket.con();
                this.message = this.socket.getMess();
            } catch (Exception e2) {
                System.out.println("\n\nException Happen");
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean loggedIn) {
            MainActivity.this.mAlertDialog.dismiss();
            ViewFlipper vf = (ViewFlipper) MainActivity.this.findViewById(R.id.viewFlipper);
            if (this.socket.get_variety() == 0) {
                vf.showNext();
                vf.showNext();
                ((TextView) MainActivity.this.findViewById(R.id.warn_message)).setText(this.message);
                return;
            }
            vf.showNext();
        }
    }
}
