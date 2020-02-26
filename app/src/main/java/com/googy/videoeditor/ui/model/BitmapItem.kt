package com.googy.videoeditor.ui.model

import android.graphics.Bitmap
import com.googy.videoeditor.ui.viewholder.ViewHolderFactory.BITMAP_VIEW_TYPE
import java.util.*

data class BitmapItem(
    val bitmap: Bitmap
) : Item() {
    private val identifier = UUID.randomUUID().toString()
    override fun getId(): String {
        return identifier
    }

    override fun equals(other: Item): Boolean {
        return other is BitmapItem && getId() == other.getId()
    }

    override fun getViewType(): Int {
        return BITMAP_VIEW_TYPE
    }

}