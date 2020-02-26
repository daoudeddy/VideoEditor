package com.googy.videoeditor.ui.edit

import android.graphics.drawable.TransitionDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST
import android.transition.AutoTransition
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.googy.common.extension.hide
import com.googy.common.extension.show
import com.googy.files.writeScaledBitmap
import com.googy.filters.gpuimage.GPUImageFilterTools
import com.googy.filters.gpuimage.GPUImageFilterTools.createFilterForType
import com.googy.filters.gpuimage.glfilters.GPUImageFilter
import com.googy.videoeditor.R
import com.googy.videoeditor.common.Extras.VIDEO_PATH
import com.googy.videoeditor.ui.adapter.Adapter
import com.googy.videoeditor.ui.base.BaseFragment
import com.googy.videoeditor.ui.bottomsheet.GenericBottomSheet
import com.googy.videoeditor.ui.bottomsheet.data.Data
import com.googy.videoeditor.ui.bottomsheet.data.Data.EDIT_SHEET
import com.googy.videoeditor.ui.extension.args
import com.googy.videoeditor.ui.extension.getBaseActivity
import com.googy.videoeditor.ui.extension.viewModelFactory
import com.googy.videoeditor.ui.model.FilterItem
import com.googy.videoeditor.ui.model.Item
import kotlinx.android.synthetic.main.edit_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import com.googy.videoeditor.ui.edit.EditViewModel.EditCommand as Command

class EditFragment : BaseFragment<Command, EditViewModel>(),
    BottomNavigationView.OnNavigationItemSelectedListener, Adapter.OnClickListener {

    private var currentFilter: GPUImageFilter? = null
    override val layoutId: Int = R.layout.edit_fragment
    override val viewModel: EditViewModel by viewModelFactory()

    private val videoPath: String by args(VIDEO_PATH, "")
    private val bottomSheet: GenericBottomSheet by lazy { GenericBottomSheet(fragmentManager!!) }

    private val thumbPath: File by lazy { File(context?.externalCacheDir, "thumb.png") }
    private val adapter: Adapter = Adapter()
    private val transition: TransitionDrawable by lazy { bottomSheetContainer.background as TransitionDrawable }


    private val slideTransition: AutoTransition by lazy {
        AutoTransition().apply {
            excludeChildren(playerWrapper, false)
            excludeChildren(bottomNavigation, true)
            addTransition(Slide(Gravity.BOTTOM))
            duration = 100
        }
    }

    override fun initUi(view: View) {
        initToolbar(toolbar)
        bottomNavigation.setOnNavigationItemSelectedListener(this)
        playerWrapper.init(videoPath)
        playerWrapper.applyFilters(getBaseActivity().getAppliedFilters())
        bottomSheet.setData(EDIT_SHEET)

        MediaMetadataRetriever().apply {
            setDataSource(videoPath)
            thumbPath.writeScaledBitmap(getFrameAtTime(10000, OPTION_CLOSEST), 120)
        }.release()

        filtersRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        filtersRecycler.adapter = adapter
        adapter.onItemClickListener(this)
        adapter.submitList(Data.getFilters(thumbPath.absolutePath))
    }

    override fun onPause() {
        super.onPause()
        hideBottomSheet()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.look -> {
                showOrHideFilters()
            }
            R.id.tools -> {
                if (filtersRecycler.isVisible) {
                    TransitionManager.beginDelayedTransition(rootView, slideTransition)
                    filtersRecycler.hide()
                }
                showBottomSheet()
            }
            R.id.export -> {
            }
        }
        return true
    }

    private fun showOrHideFilters() {
        if (filtersRecycler.isVisible) {
            TransitionManager.beginDelayedTransition(rootView, slideTransition)
            filtersRecycler.hide()
        } else {
            TransitionManager.beginDelayedTransition(rootView, slideTransition)
            hideBottomSheet()
            filtersRecycler.show()
        }
    }

    override fun <T : Item> onClick(t: T) {
        when (t) {
            is FilterItem -> {
//                cancelFilterButton.show()
//                applyFilterButton.show()
//                applyFilterButton.setAndShowEnabled(true)

                currentFilter = createFilterForType(context, t.filter)
                val adjuster = GPUImageFilterTools.FilterAdjuster(currentFilter)
                if (adjuster.canAdjust()) {
                    previewFilter(adjuster.adjuster.filter)
//                    adjusterSeekBar.progress = adjuster.adjuster.percentage.toInt()
//                    adjusterSeekBar.show()
                } else {
                    previewFilter(currentFilter)
//                    adjusterSeekBar.invisible()
                }
            }
        }
    }

    private fun showBottomSheet() {
        if (bottomSheet.isAdded) {
            hideBottomSheet()
        } else {
            filtersRecycler.hide()
            bottomSheet.show(R.id.bottomSheetContainer)
            transition.startTransition(300)
        }
    }

    private fun hideBottomSheet() {
        if (bottomSheet.isAdded) {
            bottomSheet.dismiss()
            transition.reverseTransition(300)
        }
    }

    override fun onFragmentDestroyed() {
        bottomNavigation.setOnNavigationItemSelectedListener(null)
        getBaseActivity().resetFilters()
    }

    private fun previewFilter(filter: GPUImageFilter?) {
        filter?.let {
            playerWrapper.previewFilter(it)
//            cancelFilterButton.setAndShowEnabled(true)
        }
    }

    private fun applyFilter(filter: GPUImageFilter?) {
        filter?.let {
            val adjuster = GPUImageFilterTools.FilterAdjuster(it)
            if (adjuster.canAdjust()) {
//                adjuster.adjust(adjusterSeekBar.progress.toFloat())
                playerWrapper.applyFilter(adjuster.adjuster.filter)
            } else
                playerWrapper.applyFilter(it)

//            applyFilterButton.setAndShowEnabled(false)
//            cancelFilterButton.setAndShowEnabled(false)
//            resetVisibility = true
//            activity?.invalidateOptionsMenu()
        }
    }
}
