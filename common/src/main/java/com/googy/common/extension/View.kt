package com.googy.common.extension

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun Drawable.deepCopy(): Drawable =
    constantState?.newDrawable()?.mutate() ?: throw RuntimeException("Called on null Drawable!")

fun Drawable.toGrayscale(): Drawable =
    deepCopy().apply { setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN) }

fun ImageButton.setAndShowEnabled(enabled: Boolean) {
    if (enabled == isEnabled)
        return

    isEnabled = enabled

    if (enabled) {
        setImageDrawable(tag as Drawable)
    } else {
        if (tag == null)
            tag = drawable

        setImageDrawable(drawable.toGrayscale())
    }
}