package com.example.notesapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PaintView extends View {

    private Bitmap bitmapBackground, bitmapView;
    private Paint paint = new Paint();
    private Path path = new Path();
    private int colorBackground, sizeBrush, sizeEraser;
    private float X, Y;
    private Canvas canvas;
    private final int DEFFERENCE_SPACE = 4;
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        sizeEraser = sizeBrush = 12;
        colorBackground = Color.WHITE;

        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(toPx(sizeBrush));

    }

    private float toPx(int sizeBrush) {
        return sizeBrush * (getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmapBackground = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmapView = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmapView);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmapView = bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(colorBackground);
        canvas.drawBitmap(bitmapBackground, 0, 0, null);
        canvas.drawBitmap(bitmapView, 0, 0, null);

    }

    public void setColorBackground(int color) {
        colorBackground = color;
        invalidate();
    }

    public void setSizeBrush(int size) {
        sizeBrush = size;
        paint.setStrokeWidth(toPx(sizeBrush));
    }

    public void setBrushColor(int color) {
        paint.setColor(color);
    }

    public void setSizeEraser(int size) {
        sizeEraser = size;
        paint.setStrokeWidth(toPx(sizeEraser));
    }

    public void enableEraser() {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void disableEraser() {
        paint.setXfermode(null);
        paint.setShader(null);
        paint.setMaskFilter(null);
    }

    public void addLastAction(Bitmap bitmap) {
        bitmapArrayList.add(bitmap);
    }

    public void returnLastAction() {
        if (bitmapArrayList.size() > 0) {
            bitmapArrayList.remove(bitmapArrayList.size() - 1);
            if (bitmapArrayList.size() > 0) {
                bitmapView = bitmapArrayList.get(bitmapArrayList.size() - 1);
            } else {
                bitmapView = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            }
            canvas = new Canvas(bitmapView);
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                addLastAction(getBitMap());
                break;
        }

        return true;
    }

    private void touchStart(float x, float y) {
        path.moveTo(x, y);
        X = x;
        Y = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - X);
        float dy = Math.abs(y - Y);

        if (dx > DEFFERENCE_SPACE || dy >= DEFFERENCE_SPACE) {
            path.quadTo(x, y, (x + X)/2, (y + Y)/2);
            Y = y;
            X = x;
            canvas.drawPath(path, paint);
            invalidate();
        }
    }

    private void touchUp() {

        path.reset();

    }

    public Bitmap getBitMap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bitmap;
    }

}
