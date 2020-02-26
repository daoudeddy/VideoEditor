package com.googy.videoeditor.ui.bottomsheet.data

import com.googy.filters.gpuimage.GPUImageFilterTools
import com.googy.videoeditor.R
import com.googy.videoeditor.ui.bottomsheet.model.SimpleSheetItem
import com.googy.videoeditor.ui.model.FilterItem

object Data {
    val EDIT_SHEET = listOf(
        SimpleSheetItem(
            R.string.crop,
            R.drawable.ic_crop_free_black_24dp,
            "app://editor/fragment/crop"
        ),
        SimpleSheetItem(
            R.string.trim,
            R.drawable.ic_cut_black_24dp,
            "app://editor/fragment/trim"
        ),
        SimpleSheetItem(
            R.string.rotate,
            R.drawable.ic_rotate_black_24dp,
            "app://editor/fragment/rotate"
        ),
        SimpleSheetItem(
            R.string.speed,
            R.drawable.ic_time_white_24dp,
            "app://editor/fragment/speed"
        )
    )

    fun getFilters(path: String) = listOf(
        FilterItem("NORMAL", GPUImageFilterTools.FilterType.NOFILTER, path),
        FilterItem("CONTRAST", GPUImageFilterTools.FilterType.CONTRAST, path),
        FilterItem("GRAYSCALE", GPUImageFilterTools.FilterType.GRAYSCALE, path),
        FilterItem("SHARPEN", GPUImageFilterTools.FilterType.SHARPEN, path),
        FilterItem("SEPIA", GPUImageFilterTools.FilterType.SEPIA, path),
        FilterItem("EXPOSURE", GPUImageFilterTools.FilterType.EXPOSURE, path),
        FilterItem("SATURATION", GPUImageFilterTools.FilterType.SATURATION, path),
        FilterItem("POSTERIZE", GPUImageFilterTools.FilterType.POSTERIZE, path),
        FilterItem("GAMMA", GPUImageFilterTools.FilterType.GAMMA, path),
        FilterItem("BRIGHTNESS", GPUImageFilterTools.FilterType.BRIGHTNESS, path),
        FilterItem("INVERT", GPUImageFilterTools.FilterType.INVERT, path),
        FilterItem("HUE", GPUImageFilterTools.FilterType.HUE, path),
        FilterItem("HIGHLIGHT SHADOW", GPUImageFilterTools.FilterType.HIGHLIGHT_SHADOW, path),
        FilterItem("MONOCHROME", GPUImageFilterTools.FilterType.MONOCHROME, path),
        FilterItem("WHITE BALANCE", GPUImageFilterTools.FilterType.WHITE_BALANCE, path),
        FilterItem("VIGNETTE", GPUImageFilterTools.FilterType.VIGNETTE, path),
        FilterItem("LAPLACIAN", GPUImageFilterTools.FilterType.LAPLACIAN, path),
        FilterItem("HAZE", GPUImageFilterTools.FilterType.HAZE, path),
        FilterItem("SMOOTH TOON", GPUImageFilterTools.FilterType.SMOOTH_TOON, path),
        FilterItem("TOON", GPUImageFilterTools.FilterType.TOON, path),
        FilterItem("SKETCH", GPUImageFilterTools.FilterType.SKETCH, path),
        FilterItem("BOX BLUR", GPUImageFilterTools.FilterType.BOX_BLUR, path),
        FilterItem("GAUSSIAN BLUR", GPUImageFilterTools.FilterType.GAUSSIAN_BLUR, path),
        FilterItem("PIXELATION", GPUImageFilterTools.FilterType.PIXELATION, path)
    )

}