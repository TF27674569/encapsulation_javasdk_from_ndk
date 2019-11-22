package com.ndk.sdkfromndk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ndk.opencv2.CVType;
import com.ndk.opencv2.CVUtils;
import com.ndk.opencv2.Mat;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.sample_image);
    }


    public void onClick(View view) {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        Mat src = new Mat();
        CVUtils.bitmap2Mat(bitmap, src);

        Mat dst = new Mat();
        Mat kernel = new Mat(3,3, CVType.CV_32FC1);

        kernel.put(0, 0, 0);
        kernel.put(0, 1, -1);
        kernel.put(0, 2, 0);
        kernel.put(1, 0, -1);
        kernel.put(1, 1, 5);
        kernel.put(1, 2, -1);
        kernel.put(2, 0, 0);
        kernel.put(2, 1, -1);
        kernel.put(2, 2, 0);
        int size = 15;
        /*Mat kernel = new Mat(size,size, CVType.CV_32FC1);
        float value = 1f / (size * size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel.put(i,j,value);
            }
        }*/

        CVUtils.filter2D(src, dst, kernel);

        CVUtils.mat2Bitmap(dst, bitmap);
        imageView.setImageBitmap(bitmap);
    }
}
