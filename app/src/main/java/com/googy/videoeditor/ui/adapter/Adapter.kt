package com.googy.videoeditor.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.googy.videoeditor.ui.model.Item
import com.googy.videoeditor.ui.viewholder.BaseViewHolder
import com.googy.videoeditor.ui.viewholder.ViewHolderFactory

class Adapter : ListAdapter<Item, BaseViewHolder<Item>>(DIFF_CALLBACK) {

    private var onClickListener: OnClickListener? = null

    override fun onBindViewHolder(holder: BaseViewHolder<Item>, position: Int) {
        val item = getItem(position)
        onClickListener?.let { holder.itemView.setOnClickListener { onClickListener?.onClick(item) } }
        holder.bindView(item)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getViewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Item> {

        return ViewHolderFactory.getViewHolder(parent, viewType)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Item> = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(
                oldItem: Item, newItem: Item
            ): Boolean {
                return oldItem.getId() === newItem.getId()
            }

            override fun areContentsTheSame(
                oldItem: Item, newItem: Item
            ): Boolean {
                return oldItem.equals(newItem)
            }
        }
    }

    fun onItemClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun <T : Item> onClick(t: T)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<Item>) {
        holder.onViewDetachedFromWindow(getItem(holder.adapterPosition))
        super.onViewDetachedFromWindow(holder)
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder<Item>) {
        holder.onViewAttachedToWindow(getItem(holder.adapterPosition))
        super.onViewAttachedToWindow(holder)
    }
}