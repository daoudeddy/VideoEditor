package com.googy.videoeditor.ui.model

import com.googy.filters.gpuimage.GPUImageFilterTools
import com.googy.videoeditor.ui.viewholder.ViewHolderFactory

data class FilterItem(
    val name: String,
    val filter: GPUImageFilterTools.FilterType,
    val path: String
) : Item() {
    override fun getId() = name

    override fun equals(other: Item): Boolean {
        return other is FilterItem && name == other.name
    }

    override fun getViewType(): Int {
        return ViewHolderFactory.FILTER_VIEW_TYPE
    }

}