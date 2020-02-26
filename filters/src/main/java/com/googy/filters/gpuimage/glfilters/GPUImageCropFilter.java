package com.googy.filters.gpuimage.glfilters;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GPUImageCropFilter extends GPUImageFilter {
    private CropRegion mCropRegion;

    public GPUImageCropFilter() {
        this(new CropRegion(0f, 0f, 1f, 1f));
    }

    public GPUImageCropFilter(final CropRegion cropRegion) {
        super(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
        mCropRegion = cropRegion;
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setCropRegion(mCropRegion);
    }

    public void setCropRegion(final CropRegion cropRegion) {
        mCropRegion = cropRegion;
    }

    @Override
    public void onDraw(final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {
        float minX = mCropRegion.mX;
        float minY = mCropRegion.mY;
        float maxX = mCropRegion.mX + mCropRegion.mWidth;
        float maxY = mCropRegion.mY + mCropRegion.mHeight;

        super.onDraw(textureId, convertArrayToBuffer(new float[]{
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,
        }), convertArrayToBuffer(new float[]{
                minX, minY,
                maxX, minY,
                minX, maxY,
                maxX, maxY
        }));

//        super.onDraw(textureId, convertArrayToBuffer(new float[]{
//                -1.0f, -1.0f,
//                1.0f, -1.0f,
//                -1.0f, 1.0f,
//                1.0f, 1.0f,
//        }), convertArrayToBuffer(new float[]{
//                maxX, maxY,
//                minX, maxY,
//                maxX, minY,
//                minX, minY
//        }));

    }

    private FloatBuffer convertArrayToBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(array);

        return buffer;
    }

    public static class CropRegion {
        float mX;
        float mY;
        float mWidth;
        float mHeight;

        public CropRegion(float x, float y, float width, float height) {
            mX = x;
            mY = y;
            mWidth = width;
            mHeight = height;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof GPUImageCropFilter;
    }
}