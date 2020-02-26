package com.googy.videoeditor.ui.trim

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.googy.videoeditor.ui.base.BaseViewModel

class TrimViewModel : BaseViewModel<TrimViewModel.TrimCommand>() {
    override val commandLiveData: LiveData<TrimCommand> = MutableLiveData()

    sealed class TrimCommand : Command()
}
