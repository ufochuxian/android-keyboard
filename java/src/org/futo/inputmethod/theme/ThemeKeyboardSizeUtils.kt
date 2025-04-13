package org.futo.inputmethod.theme

import android.content.Context
import android.util.Size

object ThemeKeyboardSizeUtils {
    fun getDefaultKeyboardSize(context: Context): Size {
        val displayMetrics = context.resources.displayMetrics
        val horizontalPaddingPx = (16 * displayMetrics.density).toInt()
        val defaultWidth = displayMetrics.widthPixels - horizontalPaddingPx * 2
        val defaultHeight = (250 * displayMetrics.density).toInt()
        return Size(defaultWidth, defaultHeight)
    }
}
