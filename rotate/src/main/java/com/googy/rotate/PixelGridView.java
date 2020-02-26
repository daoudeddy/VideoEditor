package com.googy.rotate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PixelGridView extends View {

    int horizontalGridCount = 8;
    private Drawable horiz;
    private Drawable vert;
    private final float width;


    public PixelGridView(@NonNull Context context) {
        this(context, null);
    }

    public PixelGridView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        horiz = new ColorDrawable(Color.WHITE);
        horiz.setAlpha(160);
        vert = new ColorDrawable(Color.WHITE);
        vert.setAlpha(160);
        width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.9f, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        horiz.setBounds(left, 0, right, (int) width);
        vert.setBounds(0, top, (int) width, bottom);
    }

    private float getLinePosition(int lineNumber) {
        int lineCount = horizontalGridCount;

        return (1f / (lineCount + 1)) * (lineNumber + 1f);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = horizontalGridCount;
        for (int n = 0; n < count; n++) {
            float pos = getLinePosition(n);
            canvas.translate(0, pos * getHeight());
            horiz.draw(canvas);
            canvas.translate(0, -pos * getHeight());
            canvas.translate(pos * getWidth(), 0);
            vert.draw(canvas);
            canvas.translate(-pos * getWidth(), 0);
        }
    }

    public void setGridCount(int gridsCount) {
        horizontalGridCount = gridsCount;
        invalidate();
    }
}