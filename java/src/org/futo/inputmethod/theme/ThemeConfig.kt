package org.futo.inputmethod.theme

import android.graphics.drawable.Drawable

data class ThemeConfig(
    var backgroundDrawable: Drawable? = null,
    var backgroundAlpha: Float = 1f,  // 0f~1f
    var keyAlpha: Float = 1f         // 0f~1f
)

