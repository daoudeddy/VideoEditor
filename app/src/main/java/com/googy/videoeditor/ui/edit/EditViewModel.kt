package com.googy.videoeditor.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.googy.videoeditor.ui.base.BaseViewModel

class EditViewModel : BaseViewModel<EditViewModel.EditCommand>() {
    override val commandLiveData: LiveData<EditCommand> = MutableLiveData()
    sealed class EditCommand : Command()

}
