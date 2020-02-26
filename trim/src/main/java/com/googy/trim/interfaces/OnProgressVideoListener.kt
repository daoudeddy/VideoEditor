package com.googy.trim.interfaces

interface OnProgressVideoListener {
    fun updateProgress(time: Int, max: Int, scale: Int)
}