package com.csc171.paintapplication.activities;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.csc171.paintapplication.R;
import com.csc171.paintapplication.models.Operation;
import com.csc171.paintapplication.views.DrawView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final String TAG = "MainActivity";

    private DrawView canvas;
    private Button button_new;
    private Button button_brush;
    private Button button_erase;
    private Button button_save;
    private Button button_undo;
    private Button button_redo;

    private SensorManager sensorManager;

    private static final double SHAKE_THRESHOLD = 25.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvas = (DrawView) findViewById(R.id.canvas);
        button_new = (Button) findViewById(R.id.button_new);
        button_brush = (Button) findViewById(R.id.button_brush);
        button_erase = (Button) findViewById(R.id.button_erase);
        button_save = (Button) findViewById(R.id.button_save);
        button_undo = (Button) findViewById(R.id.button_undo);
        button_redo = (Button) findViewById(R.id.button_redo);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCanvas();
            }
        });

        button_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCanvas();
            }
        });

        button_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.undo();
            }
        });

        button_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.redo();
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void newCanvas() {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("New Drawing");
        newDialog.setMessage("This will remove all unsaved work, do you wish to continue?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                canvas.clearCanvas();
                canvas.clearHistory();
            }
        });
        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    public void saveCanvas() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                canvas.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), canvas.getDrawingCache(), UUID.randomUUID().toString()+".png", "drawing");

                if(imgSaved != null){
                    Toast savedToast = Toast.makeText(getApplicationContext(), "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();
                }
                else {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                canvas.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
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
