package com.googy.videoeditor.ui.bottomsheet.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.googy.videoeditor.ui.bottomsheet.model.SheetItem
import com.googy.videoeditor.ui.bottomsheet.viewholder.SheetViewHolder
import com.googy.videoeditor.ui.bottomsheet.viewholder.SheetViewHolderFactory

class SheetAdapter : ListAdapter<SheetItem, SheetViewHolder<SheetItem>>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getItemViewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetViewHolder<SheetItem> {
        return SheetViewHolderFactory.getViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: SheetViewHolder<SheetItem>, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun onViewRecycled(holder: SheetViewHolder<SheetItem>) {
        super.onViewRecycled(holder)
        holder.itemView.setOnClickListener(null)
    }

    class DiffCallback : DiffUtil.ItemCallback<SheetItem>() {
        override fun areItemsTheSame(oldItem: SheetItem, newItem: SheetItem): Boolean =
            oldItem.getIdentifier() == newItem.getIdentifier()

        override fun areContentsTheSame(oldItem: SheetItem, newItem: SheetItem): Boolean =
            oldItem.isTheSameAs(newItem)
    }

}