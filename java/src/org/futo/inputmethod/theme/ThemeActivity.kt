package org.futo.inputmethod.theme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.BasicThemeProvider
import org.futo.inputmethod.latin.uix.DynamicThemeProvider
import org.futo.inputmethod.latin.uix.DynamicThemeProviderOwner
import org.futo.inputmethod.latin.uix.theme.ThemeOptions
import org.futo.inputmethod.latin.uix.theme.presets.DefaultDarkScheme

class ThemeActivity : AppCompatActivity(), DynamicThemeProviderOwner {

    private lateinit var themeProvider: BasicThemeProvider
    private var activeColorScheme = DefaultDarkScheme.obtainColors(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeProvider = BasicThemeProvider(
            context = this,
            colorScheme = activeColorScheme
        )

        setContentView(R.layout.activity_theme)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PhotoPickerFragment.newInstance())
            .commit()
    }

    override fun getDrawableProvider(): DynamicThemeProvider {
        return themeProvider
    }
}

