package e.user.client_app;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.FileNotFoundException;

public class camera extends AppCompatActivity {
    private static final int CAMERA = 66;
    private static final int PHOTO = 99;
    private ImageView mImg;
    private DisplayMetrics mPhone;
    Uri uri;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_camera);
        this.mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(this.mPhone);
        this.mImg = (ImageView) findViewById(R.id.img);
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        ((Button) findViewById(R.id.confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("imageUri", camera.this.uri.toString());
                camera.this.setResult(-1, resultIntent);
                camera.this.finish();
            }
        });
        ((Button) findViewById(R.id.photo)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction("android.intent.action.GET_CONTENT");
                camera.this.startActivityForResult(intent, 99);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != 0) {
            if ((requestCode == 66 || requestCode == 99) && data != null) {
                this.uri = data.getData();
                ContentResolver cr = getContentResolver();
                try {
                    BitmapFactory.Options mOptions = new BitmapFactory.Options();
                    mOptions.inSampleSize = 2;
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(this.uri), (Rect) null, mOptions);
                    if (bitmap.getWidth() > bitmap.getHeight()) {
                        ScalePic(bitmap, this.mPhone.heightPixels);
                    } else {
                        ScalePic(bitmap, this.mPhone.widthPixels);
                    }
                } catch (FileNotFoundException e2) {
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void ScalePic(Bitmap bitmap, int phone) {
        if (bitmap.getWidth() > phone) {
            float mScale = ((float) phone) / ((float) bitmap.getWidth());
            Matrix mMat = new Matrix();
            mMat.setScale(mScale, mScale);
            this.mImg.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMat, false));
            return;
        }
        this.mImg.setImageBitmap(bitmap);
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, (String[]) null, (String) null, (String[]) null, (String) null);
        if (cursor == null) {
            return contentURI.getPath();
        }
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();
        return result;
    }
}
