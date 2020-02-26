/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googy.filters.gpuimage.glfilters;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.googy.filters.gpuimage.util.OpenGlUtils;

import java.nio.FloatBuffer;

/**
 *
 * 因为Camera或者MediaCodec产出的Texture都是OES的，所以专门写一个来转。
 * GL program and supporting functions for textured 2D shapes.
 */
public class GPUImageExtRotationTexFilter extends GPUImageFilter {
    private static final String TAG = "GPUImageExtTexFilter";

    protected int mUniformTexMatrix;
    protected float[] mTexMatrix = null;
//    private FloatBuffer mGLTextureBuffer;

    public static final String EXT_RATATION_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "uniform mat4 uTexMatrix;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = (uTexMatrix * inputTextureCoordinate).xy;\n" +
            "}";

    // Simple fragment shader for use with external 2D textures (e.g. what we get from
    // SurfaceTexture).
    private static final String FRAGMENT_SHADER_EXT =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform samplerExternalOES inputImageTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                    "}\n";

    public GPUImageExtRotationTexFilter() {
        super(EXT_RATATION_VERTEX_SHADER, FRAGMENT_SHADER_EXT);
//        mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    public void setTexMatrix(float[] texMatrix) {
        mTexMatrix = texMatrix;
    }

    public static final float FULL_RECTANGLE_TEX_COORDS[] = {
            0.0f, 0.0f,     // 0 bottom left
            1.0f, 0.0f,     // 1 bottom right
            0.0f, 1.0f,     // 2 top left
            1.0f, 1.0f      // 3 top right
    };

    @Override
    public void onInit() {
        super.onInit();
        mUniformTexMatrix = GLES20.glGetUniformLocation(getProgram(), "uTexMatrix");
//        mGLTextureBuffer = ByteBuffer.allocateDirect(FULL_RECTANGLE_TEX_COORDS.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        mGLTextureBuffer.put(FULL_RECTANGLE_TEX_COORDS).position(0);
    }

    /**
     * Checks to see if a GLES error has been raised.
     */
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }

    // use standalone texturebuffer, don't use
    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {

        GLES20.glUseProgram(getProgram());
        runPendingOnDrawTasks();
//        if (!mIsInitialized) {
//            return;
//        }

        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(getAttribPosition(), 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);

        if (mTexMatrix != null) {
            // Copy the model / view / projection matrix over.
            GLES20.glUniformMatrix4fv(mUniformTexMatrix, 1, false, mTexMatrix, 0);
            checkGlError("mUniformTexMatrix for mMvpMatrix");
        }
        GLES20.glEnableVertexAttribArray(getAttribPosition());
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(getAttribTextureCoordinate(), 2, GLES20.GL_FLOAT, false, 0,
                textureBuffer);

        GLES20.glEnableVertexAttribArray(getAttribTextureCoordinate());
        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(getUniformTexture(), 0);
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(getAttribPosition());
        GLES20.glDisableVertexAttribArray(getAttribTextureCoordinate());
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}
