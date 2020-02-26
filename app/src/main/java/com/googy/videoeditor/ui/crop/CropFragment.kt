package com.googy.videoeditor.ui.crop

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.airbnb.deeplinkdispatch.DeepLink
import com.googy.crop.utils.CroppedRect
import com.googy.filters.gpuimage.glfilters.GPUImageCropFilter
import com.googy.videoeditor.R
import com.googy.videoeditor.common.Extras.VIDEO_PATH
import com.googy.videoeditor.ui.base.BaseFragment
import com.googy.videoeditor.ui.extension.args
import com.googy.videoeditor.ui.extension.getBaseActivity
import com.googy.videoeditor.ui.extension.viewModelFactory
import kotlinx.android.synthetic.main.crop_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import com.googy.videoeditor.ui.crop.CropViewModel.CropCommand as Command

@DeepLink("app://editor/fragment/crop")
class CropFragment : BaseFragment<Command, CropViewModel>() {

    override val viewModel: CropViewModel by viewModelFactory()
    override val layoutId: Int = R.layout.crop_fragment

    private val uri: String by args(VIDEO_PATH, "")

    override fun initUi(view: View) {
        initToolbar(toolbar)
        playerWrapper.init(uri)
        playerWrapper.applyFilters(getBaseActivity().getAppliedFilters())

        freeAspect.setOnClickListener {
            playerWrapper.setFixedAspectRatio(false)
        }
        squareAspect.setOnClickListener {
            playerWrapper.setFixedAspectRatio(true)
            playerWrapper.setAspectRatio(10, 10)
        }
        landscapeAspect.setOnClickListener {
            playerWrapper.setFixedAspectRatio(true)
            playerWrapper.setAspectRatio(16, 9)
        }
        fourByThreeAspect.setOnClickListener {
            playerWrapper.setFixedAspectRatio(true)
            playerWrapper.setAspectRatio(4, 3)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.apply_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.apply -> {
                onApplyCrop(
                    playerWrapper.getCropRect()
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onApplyCrop(rect: CroppedRect) {
        val cropFilter = GPUImageCropFilter(
            GPUImageCropFilter.CropRegion(
                rect.x,
                rect.y,
                rect.width,
                rect.height
            )
        )
        playerWrapper.onVideoSizeChanged(rect.width, rect.height)
        playerWrapper.previewFilter(cropFilter)

        getBaseActivity().addFilters(cropFilter)
    }

}
