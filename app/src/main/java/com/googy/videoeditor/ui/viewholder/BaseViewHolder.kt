package com.googy.videoeditor.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.googy.videoeditor.ui.model.Item

abstract class BaseViewHolder<T : Item>(
    layoutId: Int,
    viewGroup: ViewGroup
) : RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.context).inflate(layoutId, viewGroup, false)) {
    abstract fun bindView(item: T)
    abstract fun onViewDetachedFromWindow(item: T)
    abstract fun onViewAttachedToWindow(item: T)

    open var itemClickListener: (T) -> Unit = {}
}