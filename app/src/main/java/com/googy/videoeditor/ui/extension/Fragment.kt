package com.googy.videoeditor.ui.extension

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.googy.videoeditor.R
import com.googy.videoeditor.ui.BaseActivity

inline fun <reified T : ViewModel> Fragment.viewModelFactory(): Lazy<T> {
    return lazy {
        ViewModelProvider(
            viewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
        ).get(T::class.java)
    }
}

inline fun <reified F : Fragment> Fragment.addFragment(
    backStack: Boolean = false,
    vararg params: Pair<String, Any>
) {
    fragmentManager?.beginTransaction()?.apply {
        replace(R.id.container, newFragment<F>(*params), F::class.java.canonicalName)
        if (backStack) addToBackStack(F::class.java.canonicalName)
        commit()
    }
}

inline fun <reified T : Fragment> newFragment(vararg params: Pair<String, Any>): T {
    return T::class.java.newInstance().apply {
        arguments = bundleOf(*params)
    }
}

fun Fragment.getBaseActivity() = activity as BaseActivity

fun newFragment(clazz: Class<*>, vararg params: Pair<String, Any>): Fragment {
    return (clazz.newInstance() as Fragment).apply {
        arguments = bundleOf(*params)
    }
}

inline fun <reified T> Fragment.args(key: String, default: T) = lazy {
    (arguments?.get(key) ?: default) as T
}
