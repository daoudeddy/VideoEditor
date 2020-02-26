package com.googy.filters

/**
 * Copyright (C) 2019 Wasabeef
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.Key

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.googy.filters.gpuimage.GPUImage
import com.googy.filters.gpuimage.GPUImageFilterTools
import com.googy.filters.gpuimage.glfilters.GPUImageFilter

import java.security.MessageDigest

class GPUFilterTransformation(
    private val context: Context,
    private val filterType: GPUImageFilterTools.FilterType
) : BitmapTransformation() {

    private val gpuImageFilter: GPUImageFilter by lazy {
        GPUImageFilterTools.createFilterForType(context, filterType)
    }

    override fun toString(): String {
        return javaClass.simpleName
    }

    override fun equals(other: Any?): Boolean {
        return other is GPUFilterTransformation && other.gpuImageFilter == gpuImageFilter
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return GPUImage(context).apply {
            setImage(toTransform)
            setFilter(gpuImageFilter)
        }.bitmapWithFilterApplied
    }

    companion object {
        private const val ID = "filter transformation"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }
}