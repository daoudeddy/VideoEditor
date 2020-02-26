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

import com.googy.filters.gpuimage.glfilters.GPUImageFilter;
import com.googy.filters.gpuimage.util.OpenGlUtils;

import java.nio.FloatBuffer;


/**
 * 因为Camera或者MediaCodec产出的Texture都是OES类型的，GPUImage处理的Texture都是Texture_2D类型，所以专门写一个来转。
 * GL program and supporting functions for textured 2D shapes.
 */
public class GPUImageExtTexFilter extends GPUImageFilter {
    private static final String TAG = "GPUImageExtTexFilter";

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

    public GPUImageExtTexFilter() {
        super(GPUImageFilter.NO_FILTER_VERTEX_SHADER, FRAGMENT_SHADER_EXT);
//        mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        GLES20.glUseProgram(getProgram());
        runPendingOnDrawTasks();
//        if (!mIsInitialized) {
//            return;
//        }

        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(getAttribPosition(), 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
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
