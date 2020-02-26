package com.googy.videoeditor.ui.main

import android.Manifest
import android.content.Intent
import android.provider.DocumentsContract
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.googy.files.FileUtils
import com.googy.runtimepermission.runWithPermissions
import com.googy.videoeditor.R
import com.googy.videoeditor.common.Extras.VIDEO_PATH
import com.googy.videoeditor.ui.base.BaseFragment
import com.googy.videoeditor.ui.edit.EditFragment
import com.googy.videoeditor.ui.extension.addFragment
import com.googy.videoeditor.ui.extension.getBaseActivity
import com.googy.videoeditor.ui.extension.viewModelFactory
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import com.googy.videoeditor.ui.main.MainViewModel.MainCommand as Command


class MainFragment : BaseFragment<Command, MainViewModel>() {

    override val layoutId: Int = R.layout.main_fragment
    override val viewModel: MainViewModel by viewModelFactory()

    companion object {
        const val DIR = 1000
        const val VIDEO = 1001
    }

    override fun initUi(view: View) {
        initToolbar(toolbar, false)
        addVideoButton.setOnClickListener {
            runFileChooser()
        }
    }

    override fun onViewModelCommand(command: Command) {
        when (command) {
            is Command.Init -> {

            }
        }
    }

    private fun runDirectoryChooser() {
        runWithPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) {
            startActivityForResult(
                viewModel.getDirectoryIntent(getString(R.string.select_directory)),
                DIR
            )
        }
    }

    private fun runFileChooser() {
        runWithPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) {
            startActivityForResult(
                viewModel.getVideoIntent(getString(R.string.select_video)),
                VIDEO
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                DIR -> {
                    val uri = data?.data
                    val docUri = DocumentsContract.buildDocumentUriUsingTree(
                        uri,
                        DocumentsContract.getTreeDocumentId(uri)
                    )
                    val path = FileUtils.getPath(context, docUri)
                    handleVideoPath(path)
                }

                VIDEO -> {
                    val path = FileUtils.getPath(context, data?.data)
                    handleVideoPath(path)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleVideoPath(path: String?) {
        getBaseActivity().setVideoUri(path)
        path?.let { addFragment<EditFragment>(true, VIDEO_PATH to it) }
    }
}
