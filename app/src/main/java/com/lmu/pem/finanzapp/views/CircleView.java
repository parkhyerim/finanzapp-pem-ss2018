package com.lmu.pem.finanzapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.lmu.pem.finanzapp.model.GlobalSettings;

//derivative of original class by Michael Krause from https://stackoverflow.com/questions/25961263/draw-a-circle-onto-a-view-android/25961888
public class CircleView extends View
{
    private static final int DEFAULT_CIRCLE_COLOR = Color.RED;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private int circleColor = DEFAULT_CIRCLE_COLOR;
    private int textColor = DEFAULT_TEXT_COLOR;
    private Paint paint, textPaint, subtextPaint;
    private String text, subtext;
    private boolean emphasis;
    private int textSize, subtextSize;

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
        subtextPaint = new TextPaint();
        emphasis = false;
        textSize = 48;
        subtextSize = 38;
        text="";
        subtext="";
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
        this.emphasis = underline;
    }

    public String getSubText() {
        return subtext;
    }

    public void setSubText(String text) {
        this.subtext = text;
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
        if(emphasis){
            textPaint.setFlags(TextPaint.UNDERLINE_TEXT_FLAG);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        }
        if(subtext.length()>0){
            canvas.drawText(text.toUpperCase(), cx, cy - (textSize / 2), textPaint);

            subtextPaint.setColor(textColor);
            subtextPaint.setTextSize(subtextSize);
            subtextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(subtext, cx, cy + (textSize / 2) + 20, subtextPaint);
        } else {
            canvas.drawText(text.toUpperCase(), cx, cy + (textSize / 2), textPaint);
        }
    }
}
