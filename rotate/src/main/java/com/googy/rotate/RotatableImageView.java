package com.googy.rotate;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class RotatableImageView extends ImageView {
    private float mAngle = 0f;

    public RotatableImageView(Context context) {
        super(context);
    }

    public RotatableImageView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotatableImageView(@NonNull Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle += angle;
        Matrix matrix = new Matrix();
        matrix.postRotate(mAngle, getDrawable().getBounds().width() / 2, getDrawable().getBounds().height() / 2);
        setImageMatrix(matrix);
    }
}