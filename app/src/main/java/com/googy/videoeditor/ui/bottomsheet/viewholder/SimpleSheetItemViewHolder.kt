package com.googy.videoeditor.ui.bottomsheet.viewholder

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.googy.videoeditor.R
import com.googy.videoeditor.ui.bottomsheet.model.SimpleSheetItem
import kotlinx.android.synthetic.main.edit_sheet_item.view.*

class SimpleSheetItemViewHolder(parent: ViewGroup) :
    SheetViewHolder<SimpleSheetItem>(parent, R.layout.edit_sheet_item) {

    private val fab: AppCompatImageView = itemView.editToolIcon
    private val title: AppCompatTextView = itemView.editToolText

    override fun bindView(item: SimpleSheetItem) {
        fab.setImageResource(item.icon)
        title.setText(item.title)

        itemView.setOnClickListener {
            it.context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(item.deepLink)
                )
            )
        }
    }
}