package com.csc171.paintapplication.models;

import android.graphics.Paint;

import java.util.List;

public class Operation {
    public List<Float> points;
    public int brushColor;
    public float brushWidth;
    public Paint.Style brushStyle;

    public Operation(List<Float> points, int brushColor, float brushWidth, Paint.Style brushStyle) {
        this.points = points;
        this.brushColor = brushColor;
        this.brushWidth = brushWidth;
        this.brushStyle = brushStyle;
    }
}
