package com.googy.videoeditor.ui.viewholder

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.googy.videoeditor.R
import com.googy.videoeditor.ui.model.BitmapItem
import kotlinx.android.synthetic.main.bitmap_item.view.*

class BitmapViewHolder(parent: ViewGroup) : BaseViewHolder<BitmapItem>(R.layout.bitmap_item, parent) {
    override fun onViewAttachedToWindow(item: BitmapItem) {}

    override fun onViewDetachedFromWindow(item: BitmapItem) {
    }

    override fun bindView(item: BitmapItem) {
        Glide.with(itemView).load(item.bitmap).into(itemView.imageView)
    }
}
