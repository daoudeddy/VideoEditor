package com.googy.videoeditor.ui.bottomsheet.model

abstract class SheetItem {
    abstract fun getIdentifier(): String
    abstract fun getItemViewType(): Int
    abstract fun isTheSameAs(other: SheetItem): Boolean
    abstract fun getTargetDeepLink(): String
}