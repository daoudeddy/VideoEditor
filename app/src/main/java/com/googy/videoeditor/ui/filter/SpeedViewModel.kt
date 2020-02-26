package com.googy.videoeditor.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.googy.videoeditor.ui.base.BaseViewModel

class SpeedViewModel : BaseViewModel<SpeedViewModel.FilterCommand>() {
    override val commandLiveData: LiveData<FilterCommand> = MutableLiveData()

    sealed class FilterCommand : Command()
}
