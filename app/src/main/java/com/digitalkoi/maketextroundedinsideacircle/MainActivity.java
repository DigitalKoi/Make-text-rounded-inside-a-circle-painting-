package com.digitalkoi.maketextroundedinsideacircle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Unbinder unbinder;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.nameExTx)
    EditText nameEditText;
    @BindView(R.id.fontSizeEdTx)
    EditText fontSizeEditText;
    @BindView(R.id.radiusEdTx)
    EditText radiusEdTx;
    @BindView(R.id.spasingEdTx)
    EditText spasingEdTx;

    private int currentPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);

        currentPic = R.drawable.small_johnny_depp;
        imageView.setImageResource(R.drawable.small_johnny_depp);

    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void makeText(String someText, int textSize, int radiusManual, float spasing) {
        if (isStoragePermissionGranted()) {
            Path circle = new Path();
            Paint paint;
            Bitmap bitmap = convertToMutable(BitmapFactory.decodeResource(getResources(), currentPic));
            Canvas canvas = new Canvas(bitmap);
            float height = bitmap.getHeight();
            float radiusPic = height / 2 - radiusManual;

            circle.addCircle(radiusPic, radiusPic, radiusPic, Path.Direction.CCW);

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(Color.CYAN);
            paint.setLetterSpacing(spasing);
            paint.setTextSize(textSize);

            canvas.rotate(75, radiusPic, radiusPic);
            canvas.drawTextOnPath(someText, circle, 0, 0, paint);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Confirm access to the file system to continue", Toast.LENGTH_LONG).show();
        }
    }

    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            imgIn.recycle();
            System.gc();

            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            imgIn.copyPixelsFromBuffer(map);
            channel.close();
            randomAccessFile.close();

            file.delete();

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        return imgIn;
    }

    @OnClick(R.id.applyBt)
    public void apply(View view) {

        hideKeyboard();

        String name = String.valueOf(nameEditText.getText());
        int font = Integer.valueOf(fontSizeEditText.getText().toString());
        int radius = Integer.valueOf(radiusEdTx.getText().toString());
        float spacing = Float.valueOf(spasingEdTx.getText().toString());

        makeText(name, font, radius, spacing);
    }

    @OnClick(R.id.smallRb)
    public void smallPic(View view) {
        currentPic = R.drawable.small_johnny_depp;
        imageView.setImageResource(currentPic);
    }

    @OnClick(R.id.mediumRb)
    public void middlePic(View view) {
        currentPic = R.drawable.medium_marilyn;
        imageView.setImageResource(currentPic);
    }

    @OnClick(R.id.largeRb)
    public void largePic(View view) {
        currentPic = R.drawable.large_jack_nicholson;
        imageView.setImageResource(currentPic);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            } else {
                return true;
            }
        }
        else {
            return true;
        }
    }


}
