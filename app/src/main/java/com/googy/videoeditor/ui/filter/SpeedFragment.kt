package com.googy.videoeditor.ui.filter

import android.view.View
import com.airbnb.deeplinkdispatch.DeepLink
import com.googy.videoeditor.R
import com.googy.videoeditor.common.Extras
import com.googy.videoeditor.ui.base.BaseFragment
import com.googy.videoeditor.ui.extension.args
import com.googy.videoeditor.ui.extension.getBaseActivity
import com.googy.videoeditor.ui.extension.viewModelFactory
import kotlinx.android.synthetic.main.filters_fragment.*
import kotlinx.android.synthetic.main.toolbar.view.*

@DeepLink("app://editor/fragment/speed")
class SpeedFragment : BaseFragment<SpeedViewModel.FilterCommand, SpeedViewModel>() {

    override val viewModel: SpeedViewModel by viewModelFactory()
    override val layoutId: Int = R.layout.filters_fragment
    private val uri: String by args(Extras.VIDEO_PATH, "")

    override fun initUi(view: View) {
        initToolbar(rootView.toolbar)
        playerWrapper.init(uri)
        playerWrapper.applyFilters(getBaseActivity().getAppliedFilters())

        oneTimesButton.setOnClickListener { playerWrapper.setSpeed(1f) }
        twoTimesButton.setOnClickListener { playerWrapper.setSpeed(2f) }
        fourTimesButton.setOnClickListener { playerWrapper.setSpeed(4f) }
        sixTimesButton.setOnClickListener { playerWrapper.setSpeed(6f) }
    }
}
