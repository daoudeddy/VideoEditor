package com.googy.videoeditor.ui.main

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.googy.videoeditor.ui.base.BaseViewModel

class MainViewModel : BaseViewModel<MainViewModel.MainCommand>() {

    override val commandLiveData: LiveData<MainCommand> = MutableLiveData()

    fun getVideoIntent(title: String): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        return Intent.createChooser(intent, title)
    }

    fun getDirectoryIntent(title: String): Intent {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        i.addCategory(Intent.CATEGORY_DEFAULT)
        return Intent.createChooser(i, title)
    }

    sealed class MainCommand : Command() {
        object Init : MainCommand()
    }
}
