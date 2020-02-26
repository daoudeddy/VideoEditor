package com.googy.videoeditor.ui.bottomsheet.viewholder

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import com.googy.videoeditor.ui.bottomsheet.model.SheetItem
import com.googy.videoeditor.ui.extension.inflate

abstract class SheetViewHolder<T : SheetItem>(
        override val containerView: ViewGroup,
        @LayoutRes resId: Int
) : LayoutContainer, RecyclerView.ViewHolder(containerView.inflate(resId)) {
    abstract fun bindView(item: T)
}

