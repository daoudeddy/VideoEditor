package com.googy.videoeditor.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import com.airbnb.deeplinkdispatch.DeepLinkHandler
import com.googy.filters.gpuimage.glfilters.GPUImageFilter
import com.googy.videoeditor.R
import com.googy.videoeditor.common.Extras.VIDEO_PATH
import com.googy.videoeditor.deeplink.AppDeepLinkModule
import com.googy.videoeditor.deeplink.AppDeepLinkModuleLoader
import com.googy.videoeditor.ui.extension.addFragment
import com.googy.videoeditor.ui.main.MainFragment


@DeepLinkHandler(AppDeepLinkModule::class)
class BaseActivity : AppCompatActivity() {
    private val DISK_CACHE_SIZE = 1024 * 1024 * 50L

    private val loaders = AppDeepLinkModuleLoader()
    private var videoPath = ""
    private var filterGroup: MutableList<GPUImageFilter> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultNightMode(MODE_NIGHT_YES)

        setContentView(R.layout.main_activity)
        addFragment<MainFragment>()

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = intent.data
        if (uri?.toString()?.contains("fragment", true) == true) {
            loaders.parseUri(uri.toString())?.activityClass?.let {
                addFragment(it, true, VIDEO_PATH to videoPath)
            }
        } else {
            DeepLinkDelegate(loaders).dispatchFrom(this)
        }
    }

    fun setVideoUri(uri: String?) {
        videoPath = uri ?: ""
    }

    fun addFilters(vararg filters: GPUImageFilter) {
        filters.forEach {
            val index = filterGroup.indexOf(it)
            if (index >= 0) {
                filterGroup[index] = it
            } else {
                filterGroup.add(it)
            }
        }
    }

    fun addFilters(filters: List<GPUImageFilter>) {
        filters.forEach {
            val index = filterGroup.indexOf(it)
            if (index >= 0) {
                filterGroup[index] = it
            } else {
                filterGroup.add(it)
            }
        }
    }

    fun getAppliedFilters() = filterGroup
    fun resetFilters() = filterGroup.clear()
}
