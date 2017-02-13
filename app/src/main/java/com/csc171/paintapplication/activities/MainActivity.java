package com.csc171.paintapplication.activities;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.csc171.paintapplication.R;
import com.csc171.paintapplication.views.DrawView;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    DrawView canvas;
    Button button_new;
    Button button_brush;
    Button button_erase;
    Button button_save;

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
}
