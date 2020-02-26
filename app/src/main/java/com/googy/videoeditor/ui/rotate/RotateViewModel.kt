package com.googy.videoeditor.ui.rotate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.googy.videoeditor.ui.base.BaseViewModel

class RotateViewModel : BaseViewModel<RotateViewModel.RotateCommand>() {
    override val commandLiveData: LiveData<RotateCommand> = MutableLiveData()

    sealed class RotateCommand : Command()
}
