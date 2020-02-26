package com.googy.videoeditor.ui.model

abstract class Item {
    abstract fun getId(): String
    abstract fun equals(other: Item) : Boolean
    abstract fun getViewType() : Int
}