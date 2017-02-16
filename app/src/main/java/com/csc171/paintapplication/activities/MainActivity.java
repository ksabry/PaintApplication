package com.csc171.paintapplication.activities;

import android.content.DialogInterface;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.SeekBar;

import com.csc171.paintapplication.R;
import com.csc171.paintapplication.models.Operation;
import com.csc171.paintapplication.views.DrawView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    public static final String TAG = "MainActivity";

    private DrawView canvas;
    private Button button_new;
    private Button button_brush;
    private Button button_erase;
    private Button button_save;
    private Button button_undo;
    private Button button_redo;
    private Button button_circle_stamp;
    private ImageButton currPaint;

    private SensorManager sensorManager;

    private static final double SHAKE_THRESHOLD = 25.0;

    private float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        canvas = (DrawView) findViewById(R.id.canvas);
        button_new = (Button) findViewById(R.id.button_new);
        button_brush = (Button) findViewById(R.id.button_brush);
        button_brush.setOnClickListener(this);
        button_erase = (Button) findViewById(R.id.button_erase);
        button_erase.setOnClickListener(this);
        button_save = (Button) findViewById(R.id.button_save);
        button_undo = (Button) findViewById(R.id.button_undo);
        button_redo = (Button) findViewById(R.id.button_redo);
        button_circle_stamp = (Button) findViewById(R.id.button_circle_stamp);
        button_circle_stamp.setOnClickListener(this);


        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

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
    public void paintClicked(View view){
        if(view!=currPaint){
//update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
            canvas.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
            currPaint=(ImageButton)view;
        }

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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_brush){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush Size: " + (int)canvas.getBrushWidth());
            brushDialog.setContentView(R.layout.brush_layout);
            SeekBar seek = (SeekBar)brushDialog.findViewById(R.id.sizeSeek);
            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser){
                        canvas.setBrushWidth(progress);
                        brushDialog.setTitle("Brush Size: " + progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seek.setProgress((int) canvas.getBrushWidth());
            canvas.setErase(false);
            canvas.setCircStamp(false);
            brushDialog.show();
        }else if(v.getId() == R.id.button_erase){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush Size: " + (int)canvas.getBrushWidth());
            brushDialog.setContentView(R.layout.brush_layout);
            SeekBar seek = (SeekBar)brushDialog.findViewById(R.id.sizeSeek);
            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser){
                        canvas.setBrushWidth(progress);
                        brushDialog.setTitle("Brush Size: " + progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seek.setProgress((int) canvas.getBrushWidth());
            canvas.setErase(true);
            canvas.setCircStamp(false);
            brushDialog.show();
        }else if(v.getId() == R.id.button_circle_stamp){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setContentView(R.layout.brush_layout);
            brushDialog.setTitle("Circle Radius: " + (int)canvas.getRadius());
            SeekBar seek = (SeekBar)brushDialog.findViewById(R.id.sizeSeek);
            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        canvas.setRadius(progress);
                        brushDialog.setTitle("Circle Radius: " + canvas);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seek.setProgress((int) canvas.getBrushWidth());
            canvas.setErase(false);
            canvas.setCircStamp(true);
            brushDialog.show();
        }
    }
}
