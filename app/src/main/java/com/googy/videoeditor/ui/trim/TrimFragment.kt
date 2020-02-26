package com.googy.videoeditor.ui.trim

import android.view.View
import com.airbnb.deeplinkdispatch.DeepLink
import com.googy.trim.interfaces.OnVideoListener
import com.googy.videoeditor.R
import com.googy.videoeditor.common.Extras
import com.googy.videoeditor.ui.base.BaseFragment
import com.googy.videoeditor.ui.extension.args
import com.googy.videoeditor.ui.extension.getBaseActivity
import com.googy.videoeditor.ui.extension.viewModelFactory
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.trim_fragment.*

@DeepLink("app://editor/fragment/trim")
class TrimFragment : BaseFragment<TrimViewModel.TrimCommand, TrimViewModel>(), OnVideoListener {

    override fun onVideoPrepared() {

    }

    override val viewModel: TrimViewModel by viewModelFactory()
    override val layoutId: Int = R.layout.trim_fragment

    private val uri: String by args(Extras.VIDEO_PATH, "")

    override fun initUi(view: View) {
        initToolbar(toolbar)
        playerWrapper.init(uri)
        playerWrapper.applyFilters(getBaseActivity().getAppliedFilters())
    }

    override fun onFragmentDestroyed() {
    }
}
