package com.googy.videoeditor.ui.bottomsheet.viewholder

import android.view.ViewGroup

object SheetViewHolderFactory {
    inline fun <reified I, reified T : SheetViewHolder<I>> getViewHolder(parent: ViewGroup, viewType: Int): T {
        return when (viewType) {
            else -> SimpleSheetItemViewHolder(parent)
        } as T
    }
}