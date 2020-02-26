package com.googy.videoeditor.ui.base

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutManagerAccurateOffset(context: Context?) : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {

    private val childSizesMap = mutableMapOf<Int, Int>()

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        for (i in 0 until childCount) {
            getChildAt(i)?.let {
                childSizesMap[getPosition(it)] = it.width
            }
        }
    }

    override fun computeHorizontalScrollOffset(state: RecyclerView.State): Int {
        if (childCount == 0) {
            return 0
        }
        getChildAt(0)?.let {
            var scrolledY: Int = -it.x.toInt()
            val firstChildPosition = getPosition(it)
            for (i in 0 until firstChildPosition) {
                scrolledY += childSizesMap[i] ?: 0
            }
            return scrolledY
        }
        return 0
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

}