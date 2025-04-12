package org.futo.inputmethod.theme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.BasicThemeProvider
import org.futo.inputmethod.latin.uix.DynamicThemeProvider
import org.futo.inputmethod.latin.uix.DynamicThemeProviderOwner
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
            .add(R.id.fragment_container, PhotoPickerFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    override fun getDrawableProvider(): DynamicThemeProvider {
        return themeProvider
    }
}

