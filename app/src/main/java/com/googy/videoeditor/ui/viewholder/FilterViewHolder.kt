package com.googy.videoeditor.ui.viewholder

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.googy.filters.GPUFilterTransformation
import com.googy.videoeditor.R
import com.googy.videoeditor.ui.model.FilterItem
import kotlinx.android.synthetic.main.filter_item.view.*

class FilterViewHolder(
    parent: ViewGroup
) : BaseViewHolder<FilterItem>(R.layout.filter_item, parent) {

    override fun onViewAttachedToWindow(item: FilterItem) {
        Glide.with(itemView.glImageView)
            .asBitmap()
            .transform(RoundedCorners(30), GPUFilterTransformation(itemView.context, item.filter))
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .load(item.path)
            .into(itemView.glImageView)
    }

    override fun onViewDetachedFromWindow(item: FilterItem) {
        Glide.with(itemView.glImageView).clear(itemView.glImageView)
    }

    override fun bindView(item: FilterItem) {
        itemView.titleTextView.text = item.name
    }
}
