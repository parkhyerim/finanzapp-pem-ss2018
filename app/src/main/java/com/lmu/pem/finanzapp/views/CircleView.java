package com.lmu.pem.finanzapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

//derivative of original class by Michael Krause from https://stackoverflow.com/questions/25961263/draw-a-circle-onto-a-view-android/25961888
public class CircleView extends View
{
    private static final int DEFAULT_CIRCLE_COLOR = Color.RED;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;

    private int circleColor = DEFAULT_CIRCLE_COLOR;
    private int textColor = DEFAULT_TEXT_COLOR;
    private Paint paint, textPaint;
    private String text;
    private boolean underlineText;
    private int textSize;

    public CircleView(Context context)
    {
        super(context);
        init(context, null);
    }

    public CircleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        paint = new Paint();
        paint.setAntiAlias(true);
        textPaint = new TextPaint();
        underlineText = false;
        textSize = 60;
    }

    public void setCircleColor(int circleColor)
    {
        this.circleColor = circleColor;
        invalidate();
    }

    public int getCircleColor()
    {
        return circleColor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setText(String text, boolean underline) {
        this.text = text;
        this.underlineText = underline;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();

        int usableWidth = w - (pl + pr);
        int usableHeight = h - (pt + pb);

        int radius = Math.min(usableWidth, usableHeight) / 2;
        int cx = pl + (usableWidth / 2);
        int cy = pt + (usableHeight / 2);

        paint.setColor(circleColor);
        canvas.drawCircle(cx, cy, radius, paint);

        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        if(underlineText) textPaint.setFlags(TextPaint.UNDERLINE_TEXT_FLAG);
        canvas.drawText(text, cx, cy + (textSize / 2), textPaint);
    }
}
