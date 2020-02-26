package com.googy.videoeditor.ui.bottomsheet.data

import com.googy.videoeditor.ui.bottomsheet.model.SheetItem

abstract class DataSheet<T:SheetItem> {
    abstract fun getData(): List<T>
}