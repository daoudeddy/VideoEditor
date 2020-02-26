package com.googy.videoeditor.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.googy.videoeditor.ui.extension.getBaseActivity
import com.googy.videoeditor.ui.extension.observe
import kotlinx.coroutines.launch

abstract class BaseFragment<C : BaseViewModel.Command, VM : BaseViewModel<C>> : Fragment() {

    abstract fun initUi(view: View)
    abstract val viewModel: VM
    abstract val layoutId: Int

    open fun onFragmentDestroyed() {}
    open fun onViewModelCommand(command: C) {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutId, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.commandLiveData.observe(this, ::onViewModelCommand)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch { initUi(view) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                getBaseActivity().onBackPressed();true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.commandLiveData.removeObserver(::onViewModelCommand)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onFragmentDestroyed()
    }

    protected fun initToolbar(toolbar: Toolbar, homeAsUp: Boolean = true) {
        getBaseActivity().apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(homeAsUp)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }
}

