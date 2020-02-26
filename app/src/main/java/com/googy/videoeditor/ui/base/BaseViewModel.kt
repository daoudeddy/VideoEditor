package com.googy.videoeditor.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T : BaseViewModel.Command> : ViewModel() {
    abstract val commandLiveData: LiveData<T>

    open class Command
}