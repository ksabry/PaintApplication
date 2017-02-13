package com.csc171.paintapplication.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
    private Canvas drawCanvas;
    public Bitmap canvasBitmap;
    private Path drawPath;
    private Paint drawPaint, canvasPaint;

    private int brushColor;
    private float brushWidth;
    private Paint.Style style;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupCanvas();
    }

    private List<Float> pathPoints;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pathPoints = new ArrayList<>();
        float touchX = event.getX(), touchY = event.getY();
        pathPoints.add(touchX); pathPoints.add(touchY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            case MotionEvent.ACTION_MOVE:
                //int size = pathPoints.size();
                //drawPath.cubicTo(pathPoints.get(size - 6), pathPoints.get(size - 5), pathPoints.get(size - 4), pathPoints.get(size - 3), pathPoints.get(size - 2), pathPoints.get(size - 1));
                drawPath.lineTo(touchX, touchY);
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setupCanvas() {
        drawPath = new Path();
        drawPaint = new Paint();

        brushColor = Color.parseColor("#ff000000");
        brushWidth = 20;
        style = Paint.Style.STROKE;

        canvasPaint = new Paint(Paint.DITHER_FLAG);
        setupPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    public void setupPaint() {
        drawPaint.setColor(brushColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushWidth);
        drawPaint.setStyle(style);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setBrushColor(int color) {
        brushColor = color;
        setupPaint();
    }

    public void setBrushColor(String color) {
        setBrushColor(Color.parseColor(color));
    }

    public int getBrushColor() {
        return brushColor;
    }

    public void setBrushWidth(float width) {
        brushWidth = width;
        setupPaint();
    }

    public float getBrushWidth() {
        return brushWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }
}
