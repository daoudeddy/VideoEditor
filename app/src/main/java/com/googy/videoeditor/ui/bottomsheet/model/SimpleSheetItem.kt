package com.googy.videoeditor.ui.bottomsheet.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.googy.videoeditor.ui.bottomsheet.viewholder.SheetViewHolderType

data class SimpleSheetItem(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val deepLink: String
) : SheetItem() {
    override fun getTargetDeepLink() = deepLink

    override fun getItemViewType() = SheetViewHolderType.SimpleSheetItemViewHolder

    override fun getIdentifier() = title.toString()

    override fun isTheSameAs(other: SheetItem) =
        other is SimpleSheetItem && other.icon == icon && other.title == title && other.deepLink == deepLink
}