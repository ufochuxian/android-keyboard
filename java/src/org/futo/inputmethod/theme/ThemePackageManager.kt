package org.futo.inputmethod.theme

import android.content.Context
import java.io.File

object ThemePackageManager {
    private fun getThemeRootDir(context: Context): File {
        val themeDir = File(context.filesDir, "themes")
        if (!themeDir.exists()) {
            themeDir.mkdirs() // 自动创建目录
        }
        return themeDir
    }

    fun getThemeDir(context: Context, themeId: String): File {
        val themeRootDir = getThemeRootDir(context)
        return File(themeRootDir, themeId)
    }
}