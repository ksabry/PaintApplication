package com.csc171.paintapplication.activities;

import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.csc171.paintapplication.R;
import com.csc171.paintapplication.models.Operation;
import com.csc171.paintapplication.views.DrawView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final String TAG = "MainActivity";

    private DrawView canvas;
    private Button button_new;
    private Button button_brush;
    private Button button_erase;
    private Button button_save;

    private SensorManager sensorManager;

    private static final double SHAKE_THRESHOLD = 30.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvas = (DrawView) findViewById(R.id.canvas);
        button_new = (Button) findViewById(R.id.button_new);
        button_brush = (Button) findViewById(R.id.button_brush);
        button_erase = (Button) findViewById(R.id.button_erase);
        button_save = (Button) findViewById(R.id.button_save);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCanvas();
            }
        });

        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.clearCanvas();
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void saveCanvas() {
        try {
            File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            File f = new File(directory, "canvas.png");
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            canvas.canvasBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private long lastTime = System.currentTimeMillis();
    @Override
    public void onSensorChanged(SensorEvent event) {
        long time = System.currentTimeMillis();
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double acceleration = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
            //Log.i(TAG, "Acceleration: " + event.values[0] + ", " + event.values[1] + ", " + event.values[2]);
            if (acceleration > SHAKE_THRESHOLD) {
                onShake(event);
            }
        }
    }

    public void onShake(SensorEvent event) {
        canvas.clearCanvas();
    }
}
