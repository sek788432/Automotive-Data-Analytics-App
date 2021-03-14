package e.user.client_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class setting extends AppCompatActivity {
    public static final String FileName = "Hello";
    public static final int STATIC_INTEGER_VALUE = 2345;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int TAKE_PHOTO_IMAGE_CODE = 1234;
    byte[] bytesArray;
    String car_num;
    ImageView imageView;
    private int mPhone = 1000000;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    Uri uri;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_setting);
        Intent intent = getIntent();
        this.car_num = intent.getStringExtra("car_num");
        EditText car_number = (EditText) findViewById(R.id.editText1);
        EditText date = (EditText) findViewById(R.id.editText2);
        EditText type = (EditText) findViewById(R.id.editText3);
        EditText airFlow = (EditText) findViewById(R.id.editText4);
        EditText people = (EditText) findViewById(R.id.editText5);
        EditText fuel = (EditText) findViewById(R.id.editText6);
        EditText weight = (EditText) findViewById(R.id.editText7);
        EditText total_weight = (EditText) findViewById(R.id.editText8);
        date.setText("2019/11");
        type.setText("Mercedes-Benz");
        airFlow.setText("1950");
        people.setText("4");
        fuel.setText("超級柴油");
        weight.setText("350");
        total_weight.setText("2910");
        car_number.setText(this.car_num);
        Button CameraBtn = (Button) findViewById(R.id.button12);
        CameraBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(setting.this, camera.class);
                setting.this.startActivityForResult(intent, setting.STATIC_INTEGER_VALUE);
            }
        });
        Button GobackBtn = (Button) findViewById(R.id.button3);
        GobackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(setting.this, MainActivity.class);
                setting.this.startActivity(intent);
            }
        });
        AnonymousClass3 r10 = r0;
        final EditText editText = car_number;
        Intent intent2 = intent;
        Button SendBtn = (Button) findViewById(R.id.button4);
        final EditText editText2 = date;
        Button button = GobackBtn;
        final EditText editText3 = type;
        Button button2 = CameraBtn;
        final EditText editText4 = airFlow;
        EditText total_weight2 = total_weight;
        final EditText total_weight3 = people;
        EditText weight2 = weight;
        final EditText weight3 = fuel;
        EditText editText5 = fuel;
        final EditText fuel2 = weight2;
        EditText editText6 = people;
        final EditText people2 = total_weight2;
        AnonymousClass3 r0 = new View.OnClickListener() {
            public void onClick(View v) {
                String[] input = {editText.getText().toString(), editText2.getText().toString(), editText3.getText().toString(), editText4.getText().toString(), total_weight3.getText().toString(), weight3.getText().toString(), fuel2.getText().toString(), people2.getText().toString()};
                SocketHandler socket = new SocketHandler();
                socket.setReq(10);
                socket.setPersonal_data(input);
                socket.setPicture(setting.this.bytesArray);
                socket.con();
                new AlertDialog.Builder(setting.this).setMessage(Html.fromHtml("<font color='#ff00ddff'>已新增至資料庫</font>")).setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setting.this.finish();
                    }
                }).show();
            }
        };
        SendBtn.setOnClickListener(r10);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2345 && resultCode == -1) {
            this.uri = Uri.parse(data.getExtras().getString("imageUri"));
            try {
                this.bytesArray = getBytes(getContentResolver().openInputStream(this.uri));
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int read = inputStream.read(buffer);
            int len = read;
            if (read == -1) {
                return byteBuffer.toByteArray();
            }
            byteBuffer.write(buffer, 0, len);
        }
    }
}
