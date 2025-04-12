package org.futo.inputmethod.theme

import android.graphics.drawable.Drawable

sealed class ThemeCustomizeIntent {
    data class UpdateBrightness(val value: Float) : ThemeCustomizeIntent()
    data class UpdateOpacity(val value: Float) : ThemeCustomizeIntent()
    data class SwitchTheme(val isDark: Boolean) : ThemeCustomizeIntent()
    data class UpdateBackground(val drawable: Drawable?) : ThemeCustomizeIntent()
    object Apply : ThemeCustomizeIntent()
}


data class ThemeCustomizeState(
    val brightness: Float = 1f,
    val keyOpacity: Float = 1f,
    val isDarkTheme: Boolean = false,
    val backgroundDrawable: Drawable? = null
)

class ThemeCustomizeViewModel : BaseMviViewModel<ThemeCustomizeIntent, ThemeCustomizeState>(
    ThemeCustomizeState()
) {
    override fun handleIntent(intent: ThemeCustomizeIntent) {
        when (intent) {
            is ThemeCustomizeIntent.UpdateBrightness -> setState { it.copy(brightness = intent.value) }
            is ThemeCustomizeIntent.UpdateOpacity -> setState { it.copy(keyOpacity = intent.value) }
            is ThemeCustomizeIntent.SwitchTheme -> setState { it.copy(isDarkTheme = intent.isDark) }
            is ThemeCustomizeIntent.UpdateBackground -> setState { it.copy(backgroundDrawable = intent.drawable) }
            is ThemeCustomizeIntent.Apply -> {
                // TODO: 可存储当前 ThemeConfig
            }
        }
    }
}

