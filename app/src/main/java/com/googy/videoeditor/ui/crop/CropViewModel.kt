package com.googy.videoeditor.ui.crop

import androidx.lifecycle.MutableLiveData
import com.googy.videoeditor.ui.base.BaseViewModel

class CropViewModel : BaseViewModel<CropViewModel.CropCommand>() {
    override val commandLiveData = MutableLiveData<CropCommand>()
    sealed class CropCommand : BaseViewModel.Command()
}
