package com.googy.videoeditor.ui.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import kotlinx.coroutines.*

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)

fun View.runOnUi(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main).launch(block = block)
}

fun View.runInBackground(block: suspend CoroutineScope.() -> Unit): Job {
    return CoroutineScope(Dispatchers.IO).launch(block = block)
}

suspend fun <T> View.runWithUi(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.Main, block)
}


fun View.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}

