package org.futo.inputmethod.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import org.futo.inputmethod.theme.model.KeyboardViewStyle
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.*

object ThemePackageWriter {

    private const val TAG = "ThemePackageWriter"

    fun saveThemePackage(
        context: Context,
        keyboardViewStyle: KeyboardViewStyle,
        backgroundDrawable: Drawable?,
        backgroundAlpha: Float,
        keyAlpha: Float
    ): String? {
        val themeId = UUID.randomUUID().toString()
        val themeDir = File(context.filesDir, "themes/$themeId")
        val drawableDir = File(themeDir, "drawable")
        if (!drawableDir.mkdirs()) {
            Log.e(TAG, "Failed to create theme directories.")
            return null
        }

        // 保存背景图
        var backgroundPath: String? = null
        if (backgroundDrawable is BitmapDrawable) {
            val bitmap = backgroundDrawable.bitmap
            val bgFile = File(drawableDir, "background.png")
            FileOutputStream(bgFile).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            backgroundPath = "drawable${File.separator}background.png"
        }

        // 构造 JSON 数据结构
        val json = JSONObject().apply {
            put("themeName", "自定义主题")
            put("keyboardView", JSONObject().apply {
                put("background", JSONObject().apply {
                    put("type", "image")
                    put("imagePath", backgroundPath ?: "")
                })
                put("backgroundAlpha", (backgroundAlpha * 255).toInt())
                put("keyAlpha", (keyAlpha * 255).toInt())
                put("enterKeyBackground", keyboardViewStyle.enterKeyBackground.normal)
                put("enterKeyTextColor", keyboardViewStyle.keyTextColor)
                put("spacebarBackground", keyboardViewStyle.spacebarBackground.normal)
                put("numKeyBackground", keyboardViewStyle.numKeyBackground.normal)
                put("keyTextColor", keyboardViewStyle.keyTextColor)
                put("keyTextColorPressed", keyboardViewStyle.keyTextColorPressed)
                put("functionalKeyBackground", keyboardViewStyle.functionalKeyBackground.normal)
                put("functionalKeyTextColor", keyboardViewStyle.functionalKeyTextColor)
            })
        }

        // 保存 JSON 文件
        val jsonFile = File(themeDir, "theme.json")
        jsonFile.writeText(json.toString(4)) // pretty print

        Log.d(TAG, "Theme package saved at: ${themeDir.absolutePath}")
        return themeId
    }
}


