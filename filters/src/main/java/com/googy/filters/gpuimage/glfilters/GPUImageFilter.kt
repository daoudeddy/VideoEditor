/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googy.filters.gpuimage.glfilters

import android.content.Context
import android.graphics.PointF
import android.opengl.GLES20
import com.googy.filters.gpuimage.util.OpenGlUtils
import java.io.InputStream
import java.io.Serializable
import java.nio.FloatBuffer
import java.util.*

open class GPUImageFilter @JvmOverloads constructor(
    private val mVertexShader: String = NO_FILTER_VERTEX_SHADER,
    private val mFragmentShader: String = NO_FILTER_FRAGMENT_SHADER
) : Serializable {

    private val mRunOnDraw: LinkedList<Runnable> = LinkedList()

    var program: Int = 0
        protected set
    var attribPosition: Int = 0
        protected set
    var uniformTexture: Int = 0
        protected set
    var attribTextureCoordinate: Int = 0
        protected set
    var outputWidth: Int = 0
        protected set
    var outputHeight: Int = 0
        protected set
    var isInitialized: Boolean = false
        private set

    fun init() {
        onInit()
        isInitialized = true
        onInitialized()
    }

    open fun onInit() {
        program = OpenGlUtils.loadProgram(mVertexShader, mFragmentShader)
        attribPosition = GLES20.glGetAttribLocation(program, "position")
        uniformTexture = GLES20.glGetUniformLocation(program, "inputImageTexture")
        attribTextureCoordinate = GLES20.glGetAttribLocation(program, "inputTextureCoordinate")
        isInitialized = true
    }

    open fun onInitialized() {}

    fun destroy() {
        isInitialized = false
        GLES20.glDeleteProgram(program)
        onDestroy()
    }

    open fun onDestroy() {}

    open fun onOutputSizeChanged(width: Int, height: Int) {
        outputWidth = width
        outputHeight = height
    }

    open fun onDraw(
        textureId: Int, cubeBuffer: FloatBuffer,
        textureBuffer: FloatBuffer
    ) {
        GLES20.glUseProgram(program)
        runPendingOnDrawTasks()
        if (!isInitialized) {
            return
        }

        cubeBuffer.position(0)
        GLES20.glVertexAttribPointer(attribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer)
        GLES20.glEnableVertexAttribArray(attribPosition)
        textureBuffer.position(0)
        GLES20.glVertexAttribPointer(
            attribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
            textureBuffer
        )
        GLES20.glEnableVertexAttribArray(attribTextureCoordinate)
        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glUniform1i(uniformTexture, 0)
        }
        onDrawArraysPre()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(attribPosition)
        GLES20.glDisableVertexAttribArray(attribTextureCoordinate)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    protected open fun onDrawArraysPre() {}

    protected fun runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run()
        }
    }

    protected fun setInteger(location: Int, intValue: Int) {
        runOnDraw(Runnable { GLES20.glUniform1i(location, intValue) })
    }

    fun setFloat(location: Int, floatValue: Float) {
        runOnDraw(Runnable { GLES20.glUniform1f(location, floatValue) })
    }

    protected fun setFloatVec2(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setFloatVec3(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setFloatVec4(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setFloatArray(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable {
            GLES20.glUniform1fv(
                location,
                arrayValue.size,
                FloatBuffer.wrap(arrayValue)
            )
        })
    }

    protected fun setPoint(location: Int, point: PointF) {
        runOnDraw(Runnable {
            val vec2 = FloatArray(2)
            vec2[0] = point.x
            vec2[1] = point.y
            GLES20.glUniform2fv(location, 1, vec2, 0)
        })
    }

    protected fun setUniformMatrix3f(location: Int, matrix: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0) })
    }

    protected fun setUniformMatrix4f(location: Int, matrix: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0) })
    }

    protected fun runOnDraw(runnable: Runnable) {
        synchronized(mRunOnDraw) {
            mRunOnDraw.addLast(runnable)
        }
    }

    companion object {
        const val NO_FILTER_VERTEX_SHADER = "" +
                "attribute vec4 position;\n" +
                "attribute vec4 inputTextureCoordinate;\n" +
                " \n" +
                "varying vec2 textureCoordinate;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = position;\n" +
                "    textureCoordinate = inputTextureCoordinate.xy;\n" +
                "}"
        const val NO_FILTER_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                " \n" +
                "uniform sampler2D inputImageTexture;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "}"

        fun loadShader(file: String, context: Context): String {
            return try {
                val assetManager = context.assets
                val ims = assetManager.open(file)
                val re = convertStreamToString(ims)
                ims.close()
                re
            } catch (e: Exception) {
                e.printStackTrace()
                String()
            }
        }

        private fun convertStreamToString(`is`: InputStream): String {
            val s = java.util.Scanner(`is`).useDelimiter("\\A")
            return if (s.hasNext()) s.next() else ""
        }
    }
}
