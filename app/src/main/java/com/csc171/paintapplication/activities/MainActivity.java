package com.csc171.paintapplication.activities;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.csc171.paintapplication.R;
import com.csc171.paintapplication.views.DrawView;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    DrawView canvas;
    Button button_new;
    Button button_brush;
    Button button_erase;
    Button button_save;

    private float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvas = (DrawView) findViewById(R.id.canvas);
        button_new = (Button) findViewById(R.id.button_new);
        button_brush = (Button) findViewById(R.id.button_brush);
        button_brush.setOnClickListener(this);
        button_erase = (Button) findViewById(R.id.button_erase);
        button_erase.setOnClickListener(this);
        button_save = (Button) findViewById(R.id.button_save);

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCanvas();
            }
        });
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
            canvas.setBrushColor(Color.parseColor("#ff000000"));
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
            canvas.setBrushColor(Color.TRANSPARENT);
            brushDialog.show();
        }
    }
}
