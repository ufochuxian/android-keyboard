package org.futo.inputmethod.theme

import android.graphics.drawable.Drawable

data class ThemeConfig(
    val backgroundDrawable: Drawable? = null,
    val backgroundAlpha: Float = 1f,  // 0f~1f
    val keyAlpha: Float = 1f         // 0f~1f
)

