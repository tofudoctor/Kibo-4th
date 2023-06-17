package com.example.photodetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), , options);

        // Log.i("NeverCaresYou", String.valueOf(bitmap.getHeight()));
        // Log.i("NeverCaresYou", String.valueOf(bitmap.getWidth()));

        if (! OpenCVLoader.initDebug()) {
            Log.e("NeverCaresYou", "init fail");
            return;
        }

        Mat rgba = null;
        try {
            rgba = Utils.loadResource(MainActivity.this, R.drawable.photo4, CvType.CV_8UC4);
        }
        catch(IOException e) {
            e.printStackTrace();
            return;
        }


        Log.d("NeverCaresYou", "size" + rgba.size());
        Log.i("NeverCaresYou", String.valueOf(rgba.channels()));

        Mat gray = new Mat();
        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGBA2GRAY);
        Log.i("NeverCaresYou", String.valueOf(gray.channels()));

        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        LinkedList<Mat> corners = new LinkedList<>();
        MatOfInt ids = new MatOfInt();


        Aruco.detectMarkers(gray, dictionary, corners, ids);

        for (int i = 0 ; i < corners.size() ; i++) {
            Log.i("NeverCaresYou", corners.get(i).dump());
        }

        Log.i("NeverCaresYou", ids.dump());

    }
}
