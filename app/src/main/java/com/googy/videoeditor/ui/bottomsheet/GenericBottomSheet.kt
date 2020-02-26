package com.googy.videoeditor.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.googy.videoeditor.R
import com.googy.videoeditor.ui.bottomsheet.adapter.SheetAdapter
import com.googy.videoeditor.ui.bottomsheet.data.DataSheet
import com.googy.videoeditor.ui.bottomsheet.model.SheetItem
import kotlinx.android.synthetic.main.bottom_sheet.*

class GenericBottomSheet(
    private val fm: FragmentManager?,
    private val spanSize: Int = 3
) : BottomSheetDialogFragment() {

    private var isShown: Boolean = false
    private val TAG: String? = GenericBottomSheet::class.java.canonicalName

    private val adapter = SheetAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.apply {
            layoutManager = GridLayoutManager(context, spanSize)
            this.adapter = this@GenericBottomSheet.adapter
        }
    }

    fun <T : SheetItem> setData(dataSheet: DataSheet<T>?) {
        adapter.submitList(dataSheet?.getData())
    }

    fun setData(data: List<SheetItem>?) {
        adapter.submitList(data)
    }

    fun show(container: Int) {
        isShown = true
        fm?.let {
            fm.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom,
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom
                ).apply {
                    if (!isAdded) {
                        add(container, this@GenericBottomSheet, TAG)
                        addToBackStack(TAG)
                        commit()
                    } else {
                        dismiss()
                    }
                }
        }
    }

    override fun onDetach() {
        super.onDetach()
        isShown
    }

    override fun onDestroy() {
        super.onDestroy()
        isShown = false
    }

    override fun dismiss() {
        if (isShown) fm?.let {
            fm.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom,
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom
                )
                .remove(this)
                .commit()
            it.popBackStack()
            isShown = false
        }
    }
}
