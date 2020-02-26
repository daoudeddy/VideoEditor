package com.googy.videoeditor.ui.viewholder

import android.view.ViewGroup


object ViewHolderFactory {
    inline fun <reified I, reified T : BaseViewHolder<I>> getViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): T {
        return (when (viewType) {
            FILTER_VIEW_TYPE -> FilterViewHolder(parent)
            BITMAP_VIEW_TYPE -> BitmapViewHolder(parent)
            else -> EmptyViewHolder(parent)
        } as T)
    }

    const val FILTER_VIEW_TYPE = 10
    const val BITMAP_VIEW_TYPE = 20
}