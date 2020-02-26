package com.googy.videoeditor.ui.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.googy.videoeditor.R


inline fun <reified F : Fragment> FragmentActivity.addFragment(
    backStack: Boolean = false,
    vararg params: Pair<String, Any>
) {
    supportFragmentManager?.beginTransaction()?.apply {
        add(R.id.container, newFragment<F>(*params), F::class.java.canonicalName)
        if (backStack) addToBackStack(F::class.java.canonicalName)
        commit()
    }
}

fun FragmentActivity.addFragment(
    clazz: Class<*>,
    backStack: Boolean = false,
    vararg params: Pair<String, Any>
) {
    supportFragmentManager?.beginTransaction()?.apply {
        replace(R.id.container, newFragment(clazz, *params), clazz.canonicalName)
        if (backStack) addToBackStack(clazz.canonicalName)
        commit()
    }
}

inline fun <reified T> FragmentActivity.intent(key: String, default: T) = lazy {
    val value = (intent?.extras?.get(key) ?: default)
    return@lazy when {
        default is Boolean && value is String -> {
            value.toBoolean() as T
        }
        else -> {
            value as T
        }
    }
}
