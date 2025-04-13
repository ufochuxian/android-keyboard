package org.futo.inputmethod.theme

import android.content.Context
import android.graphics.drawable.Drawable


import org.futo.inputmethod.theme.model.BackgroundStyle
import org.futo.inputmethod.theme.model.ColorState
import org.json.JSONObject

data class ThemeConfig(
    var backgroundDrawable: Drawable? = null,
    var backgroundStyle: BackgroundStyle = BackgroundStyle.Color("#FFFFFF"),
    var backgroundAlpha: Float = 1f,  // 0f~1f
    var keyAlpha: Float = 1f,         // 0f~1f

    // 按键背景相关
    var keyBackground: ColorState = ColorState("#DADADA", "#AAAAAA", 7f),
    var functionalKeyBackground: ColorState = ColorState("#3D3D3D", "#222222", 7f),
    var spacebarBackground: ColorState = ColorState("#2C2C2C", "#1F1F1F", 7f),
    var enterKeyBackground: ColorState = ColorState("#2C2C2C", "#1F1F1F", 7f),
    var numKeyBackground: ColorState = ColorState("#2C2C2C", "#1F1F1F", 7f),

    // 按键字体颜色
    var keyTextColor: String = "#000000",
    var keyTextColorPressed: String = "#444444",
    var functionalKeyTextColor: String = "#DADADA"
)


fun ThemeConfig.toJson(themeName: String = "自定义主题"): JSONObject {
    return JSONObject().apply {
        put("themeName", themeName)
        put("keyboardView", JSONObject().apply {
            put("background", JSONObject().apply {
                when (val bg = backgroundStyle) {
                    is BackgroundStyle.Color -> {
                        put("type", "color")
                        put("color", bg.color)
                    }
                    is BackgroundStyle.Gradient -> {
                        put("type", "gradient")
                        put("startColor", bg.startColor)
                        put("endColor", bg.endColor)
                        put("orientation", bg.orientation)
                    }
                    is BackgroundStyle.Image -> {
                        put("type", "image")
                        put("imagePath", bg.imagePath)
                    }
                }
            })
            put("backgroundAlpha", (backgroundAlpha * 255).toInt())
            put("keyAlpha", (keyAlpha * 255).toInt())

            // 按键背景颜色
            put("enterKeyBackground", enterKeyBackground.normal)
            put("enterKeyTextColor", keyTextColor)
            put("spacebarBackground", spacebarBackground.normal)
            put("numKeyBackground", numKeyBackground.normal)
            put("keyTextColor", keyTextColor)
            put("keyTextColorPressed", keyTextColorPressed)
            put("functionalKeyBackground", functionalKeyBackground.normal)
            put("functionalKeyTextColor", functionalKeyTextColor)
        })
    }
}

fun ThemeConfig.fromJson(context: Context, themeId: String, json: JSONObject): ThemeConfig {
    val keyboardView = json.getJSONObject("keyboardView")

    // 解析背景样式
    val backgroundObj = keyboardView.getJSONObject("background")
    val backgroundStyle = when (backgroundObj.getString("type")) {
        "color" -> BackgroundStyle.Color(backgroundObj.getString("color"))
        "gradient" -> BackgroundStyle.Gradient(
            startColor = backgroundObj.getString("startColor"),
            endColor = backgroundObj.getString("endColor"),
            orientation = backgroundObj.getString("orientation")
        )
        "image" -> BackgroundStyle.Image(backgroundObj.getString("imagePath"))
        else -> BackgroundStyle.Color("#FFFFFF") // fallback
    }

    val backgroundAlpha = keyboardView.optInt("backgroundAlpha", 255) / 255f
    val keyAlpha = keyboardView.optInt("keyAlpha", 255) / 255f

    // 加载背景 Drawable
    val drawable: Drawable? = backgroundStyle.toDrawable(context, themeId)

    return ThemeConfig(
        backgroundDrawable = drawable,
        backgroundStyle = backgroundStyle,
        backgroundAlpha = backgroundAlpha,
        keyAlpha = keyAlpha,

        keyBackground = ColorState(
            normal = keyboardView.optString("keyBackground", "#DADADA"),
            pressed = keyboardView.optString("keyBackgroundPressed", "#AAAAAA"),
        ),
        functionalKeyBackground = ColorState(
            normal = keyboardView.optString("functionalKeyBackground", "#3D3D3D"),
            pressed = keyboardView.optString("functionalKeyBackgroundPressed", "#2A2A2A"),
        ),
        spacebarBackground = ColorState(
            normal = keyboardView.optString("spacebarBackground", "#2C2C2C"),
            pressed = keyboardView.optString("spacebarBackgroundPressed", "#1F1F1F"),
        ),
        enterKeyBackground = ColorState(
            normal = keyboardView.optString("enterKeyBackground", "#2C2C2C"),
            pressed = keyboardView.optString("enterKeyBackgroundPressed", "#1F1F1F"),
        ),
        numKeyBackground = ColorState(
            normal = keyboardView.optString("numKeyBackground", "#2C2C2C"),
            pressed = keyboardView.optString("numKeyBackgroundPressed", "#1F1F1F"),
        ),

        keyTextColor = keyboardView.optString("keyTextColor", "#000000"),
        keyTextColorPressed = keyboardView.optString("keyTextColorPressed", "#444444"),
        functionalKeyTextColor = keyboardView.optString("functionalKeyTextColor", "#DADADA")
    )
}



