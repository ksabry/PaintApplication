package com.csc171.paintapplication.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


import com.csc171.paintapplication.models.Operation;

import com.csc171.paintapplication.R;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View{
    public static final String TAG = "DrawView";


    private Canvas drawCanvas;
    public Bitmap canvasBitmap;
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int radius = 40;
    private boolean isCircleStamp = false;

    public int brushColor = 0xFF660000;
    private float brushWidth,lastBrushSize;
    private Paint.Style style;

    List<Operation> history;
    List<Bitmap> historyCache;
    int historyIndex = 0;
    private static final int historyCacheInterval = 10;

    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.f;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupCanvas();
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    private List<Float> pathPoints;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        
        float touchX = event.getX(), touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!isCircleStamp) {
                    pathPoints = new ArrayList<>();
                    drawPath.moveTo(touchX, touchY);
                    pathPoints.add(touchX);
                    pathPoints.add(touchY);
                }else {
                    drawCanvas.drawCircle(touchX, touchY, radius, drawPaint);
                }

                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                addToHistory();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isCircleStamp) {
                    pathPoints.add(touchX);
                    pathPoints.add(touchY);
                    drawPath.lineTo(touchX, touchY);
                }
                //int size = pathPoints.size();
                //drawPath.cubicTo(pathPoints.get(size - 6), pathPoints.get(size - 5), pathPoints.get(size - 4), pathPoints.get(size - 3), pathPoints.get(size - 2), pathPoints.get(size - 1));
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    private void addToHistory() {
        history = history.subList(0, historyIndex);
        int historyCacheIndex = historyIndex / historyCacheInterval;
        historyCache = historyCache.subList(0, historyCacheIndex + 1);

        historyIndex++;
        history.add(new Operation(pathPoints, brushColor, brushWidth, style));
        if (historyIndex % historyCacheInterval == 0) {
            Bitmap bitmapCache = canvasBitmap.copy(canvasBitmap.getConfig(), true);
            historyCache.add(bitmapCache);
        }
    }

    public void recoverFromHistory(int index) {
        int historyCacheIndex = index / historyCacheInterval;
        drawCanvas.drawColor(Color.WHITE);
        drawCanvas.drawBitmap(historyCache.get(historyCacheIndex), 0, 0, drawPaint);
        for (int idx = historyCacheIndex * historyCacheInterval; idx < index; idx++) {
            applyOperation(history.get(idx));
        }
        invalidate();
    }

    public void applyOperation(Operation operation) {
        drawPaint.setColor(operation.brushColor);
        drawPaint.setStrokeWidth(operation.brushWidth);
        drawPaint.setStyle(operation.brushStyle);
        drawPath.moveTo(operation.points.get(0), operation.points.get(1));
        for (int idx = 2; idx < operation.points.size(); idx += 2) {
            drawPath.lineTo(operation.points.get(idx), operation.points.get(idx + 1));
        }
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();

        setupPaint();
    }

    public void setupCanvas() {
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(brushColor);
        brushWidth = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushWidth;
        style = Paint.Style.STROKE;

        canvasPaint = new Paint(Paint.DITHER_FLAG);
        setupPaint();

    }

    public void clearHistory() {
        history.clear();
        historyCache.clear();
        historyCache.add(canvasBitmap.copy(canvasBitmap.getConfig(), true));
        historyIndex = 0;
    }

    public void clearCanvas() {
        if (drawCanvas != null) {
            drawCanvas.drawColor(Color.WHITE);
            invalidate();
        }
    }

    public void undo() {
        if (historyIndex != 0)
            recoverFromHistory(--historyIndex);
    }

    public void redo() {
        if (historyIndex != history.size()) {
            applyOperation(history.get(historyIndex++));
            invalidate();
        }
    }

    public void setColor(String newColor){
//set color
        invalidate();
        brushColor = Color.parseColor(newColor);
        drawPaint.setColor(brushColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        if (historyIndex > 0) {
            Log.e(TAG, "When does this happen?");
        }
        else {
            history = new ArrayList<>();
            historyCache = new ArrayList<>();
            historyCache.add(canvasBitmap.copy(canvasBitmap.getConfig(), true));
        }
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

    public void setErase(boolean b){
        if(b){
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        }else{
            drawPaint.setXfermode(null);
        }

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

    public int getRadius (){
        return this.radius;
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    public void setCircStamp(boolean isSelected){
        isCircleStamp = isSelected;
    }

    public float getBrushWidth() {
        return brushWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        canvas.restore();
    }

    public void setLastBrushWidth(float lastBrushWidth) {
        lastBrushSize = lastBrushWidth;
    }

    public void placeText(String s, int xVal, int yVal) {

        drawCanvas.save();
        drawCanvas.scale(scaleFactor,scaleFactor);
        drawCanvas.drawBitmap(canvasBitmap, 0,0, canvasPaint);
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setStrokeWidth(1);
        drawPaint.setTextSize(50);
        drawCanvas.drawText(s,xVal,yVal,drawPaint);
        drawPaint.setStrokeWidth(lastBrushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawCanvas.restore();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector){
            return true;
        }
    }
}
