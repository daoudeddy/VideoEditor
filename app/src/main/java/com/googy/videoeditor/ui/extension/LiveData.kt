package com.googy.videoeditor.ui.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.googy.videoeditor.ui.base.BaseViewModel


fun <T : BaseViewModel.Command> LiveData<T>.observe(
    baseFragment: Fragment,
    block: (T) -> Unit
) {
    observe(baseFragment, Observer(block))
}
