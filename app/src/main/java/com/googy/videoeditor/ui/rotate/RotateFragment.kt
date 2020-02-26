package com.googy.videoeditor.ui.rotate

import android.media.MediaMetadataRetriever
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.airbnb.deeplinkdispatch.DeepLink
import com.googy.filters.gpuimage.GPUImageFilterTools
import com.googy.filters.gpuimage.glfilters.GPUImageTransformFilter
import com.googy.rotate.HorizontalProgressWheelView
import com.googy.videoeditor.R
import com.googy.videoeditor.common.Extras
import com.googy.videoeditor.ui.base.BaseFragment
import com.googy.videoeditor.ui.extension.args
import com.googy.videoeditor.ui.extension.getBaseActivity
import com.googy.videoeditor.ui.extension.viewModelFactory
import kotlinx.android.synthetic.main.rotate_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

@DeepLink("app://editor/fragment/rotate")
class RotateFragment : BaseFragment<RotateViewModel.RotateCommand, RotateViewModel>() {

    override val viewModel: RotateViewModel by viewModelFactory()
    override val layoutId: Int = R.layout.rotate_fragment
    private val uri: String by args(Extras.VIDEO_PATH, "")
    private val retriever = MediaMetadataRetriever()
    private val filterAdjuster = GPUImageFilterTools.FilterAdjuster(GPUImageTransformFilter())
    private var currentAngel = 0f

    override fun initUi(view: View) {
        initToolbar(toolbar)
        retriever.setDataSource(uri)
        playerWrapper.init(uri)
        playerWrapper.applyFilters(getBaseActivity().getAppliedFilters())

        resetButton.setOnClickListener {
            resetRotation()
        }

        rotateButton.setOnClickListener {
            filterAdjuster.adjust(-setCurrentAngel(currentAngel + 90))
            setRotationText()
            playerWrapper.previewFilter(filterAdjuster.adjuster.filter)
        }

        rotationWheel.setScrollingListener(object : HorizontalProgressWheelView.ScrollingListener {
            override fun onScrollStart() {
                playerWrapper.showGuideLines()
            }

            override fun onScrollEnd() {
                playerWrapper.hideGuideLines()
            }

            override fun onScroll(delta: Float, totalDistance: Float) {
                filterAdjuster.adjust(-setCurrentAngel(currentAngel + (delta / 42)))
                setRotationText()
                playerWrapper.previewFilter(filterAdjuster.adjuster.filter)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.apply_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.apply -> {
                onApplyRotation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onApplyRotation() {
        getBaseActivity().addFilters(filterAdjuster.adjuster.filter)
    }

    private fun resetRotation() {
        filterAdjuster.adjust(-setCurrentAngel(0f))
        playerWrapper.previewFilter(filterAdjuster.adjuster.filter)
        setRotationText()
    }

    private fun setRotationText() {
        rotationText.text = String.format(Locale.getDefault(), "%.1f°", currentAngel)
    }

    private fun setCurrentAngel(angel: Float): Float {
        currentAngel = when {
            angel in -359.0f..359.0f -> angel
            angel > 0 -> angel - 360f
            else -> angel + 360f
        }
        return currentAngel
    }
}
