package org.futo.inputmethod.theme.model

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.core.graphics.toColorInt
import org.futo.inputmethod.theme.Env
import org.futo.inputmethod.theme.ThemePackageManager
import java.io.File

data class KeyboardThemeConfig(
    val themeId: String,
    val keyboardView: KeyboardViewStyle
) {
    fun getKeyBackgroundDrawable(): Drawable =
        createStateListDrawable(
            keyboardView.keyBackground.normal,
            keyboardView.keyBackground.pressed,
            keyboardView.keyBackground.cornerRadius
        )

    fun getFunctionalKeyBackgroundDrawable(): Drawable =
        createStateListDrawable(
            keyboardView.functionalKeyBackground.normal,
            keyboardView.functionalKeyBackground.pressed,
            keyboardView.functionalKeyBackground.cornerRadius
        )

    fun getSpacebarBackgroundDrawable(): Drawable =
        createStateListDrawable(
            keyboardView.spacebarBackground.normal,
            keyboardView.spacebarBackground.pressed,
            keyboardView.spacebarBackground.cornerRadius
        )

    fun getEnterKeyBackgroundDrawable(): Drawable =
        createStateListDrawable(
            keyboardView.enterKeyBackground.normal,
            keyboardView.enterKeyBackground.pressed,
            keyboardView.enterKeyBackground.cornerRadius
        )

    fun getNumKeyBackgroundDrawable(): Drawable =
        createStateListDrawable(
            keyboardView.numKeyBackground.normal,
            keyboardView.numKeyBackground.pressed,
            keyboardView.numKeyBackground.cornerRadius
        )

}

fun createStateListDrawable(normalColor: String, pressedColor: String, cornerRadius: Float): StateListDrawable {
    return StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_pressed), createRoundedDrawable(pressedColor, cornerRadius))
        addState(intArrayOf(), createRoundedDrawable(normalColor, cornerRadius))
    }
}

private fun createRoundedDrawable(color: String, cornerRadius: Float): Drawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(color.toColorInt())
        val radiusPx = cornerRadius * Env.context.resources.displayMetrics.density
        this.cornerRadius = radiusPx
    }
}


data class ColorState(
    val normal: String,
    val pressed: String,
    val cornerRadius: Float = 7f // 单位：dp
)


data class KeyboardViewStyle(
    val background: BackgroundStyle,
    val keyBackground: ColorState,
    val functionalKeyBackground: ColorState,
    val spacebarBackground: ColorState,
    val enterKeyBackground: ColorState,
    val numKeyBackground: ColorState,
    val keyTextColor: String,
    val keyTextColorPressed: String,
    val functionalKeyTextColor: String
)

sealed class BackgroundStyle {
    data class Color(val color: String) : BackgroundStyle()
    data class Gradient(val startColor: String, val endColor: String, val orientation: String) : BackgroundStyle()
    data class Image(val imagePath: String) : BackgroundStyle()

    fun toDrawable(context: Context, themeId: String): Drawable? {
        return when (this) {
            is Color -> ColorDrawable(color.toColorInt())
            is Gradient -> {
                val orientation = when (orientation.lowercase()) {
                    "tl_br" -> GradientDrawable.Orientation.TL_BR
                    "tr_bl" -> GradientDrawable.Orientation.TR_BL
                    "bl_tr" -> GradientDrawable.Orientation.BL_TR
                    "br_tl" -> GradientDrawable.Orientation.BR_TL
                    else -> GradientDrawable.Orientation.TOP_BOTTOM
                }
                GradientDrawable(
                    orientation, intArrayOf(
                        startColor.toColorInt(),
                        endColor.toColorInt()
                    )
                )
            }

            is Image -> {
                val themeDir = ThemePackageManager.getThemeDir(context, themeId)
                val file = File(themeDir, imagePath)
                if (file.exists()) Drawable.createFromPath(file.absolutePath) else null
            }
        }
    }

}