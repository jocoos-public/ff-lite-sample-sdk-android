package com.jocoos.flipflop.sample.utils

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Date
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("DEPRECATION")
fun Activity.makeStatusBarTransparent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.apply {
            statusBarColor = Color.TRANSPARENT
            setDecorFitsSystemWindows(false)
        }
    } else {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.TRANSPARENT
        }
    }
}

fun View.setMarginTop(marginTop: Int) {
    val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    menuLayoutParams.setMargins(0, marginTop, 0, 0)
    this.layoutParams = menuLayoutParams
}

fun View.setMarginBottom(marginBottom: Int) {
    val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    menuLayoutParams.setMargins(0, 0, 0, marginBottom)
    this.layoutParams = menuLayoutParams
}

val Int.px: Int get() = ((this * Resources.getSystem().displayMetrics.density).toInt())

typealias OnRecyclerViewItemClickListener = (View, Int) -> Unit

inline fun ViewModel.onLaunch(
    dispatcher: CoroutineContext = EmptyCoroutineContext,
    crossinline body: suspend CoroutineScope.() -> Unit
) = viewModelScope.launch(dispatcher) {
    body(this)
}

fun Fragment.launchAndRepeatOnLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(state) {
            action()
        }
    }
}

fun TextView.onTextChanged(
    before: (string: String, start: Int, count: Int, after: Int) -> Unit = { _, _, _, _ -> },
    after: (s: Editable) -> Unit = {},
    onTextChanged: (string: String, start: Int, before: Int, count: Int) -> Unit
) = addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable) = after(s)
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
        before(s.toString(), start, count, after)

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) =
        onTextChanged(s.toString(), start, before, count)
})

fun String.toDateTime(): Date {
    return DateTimeUtils
        .toDate(ZonedDateTime.parse(this, DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault())).toInstant())
}
