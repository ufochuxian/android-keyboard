package org.futo.inputmethod.theme

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.SeekBar
import androidx.compose.material3.lightColorScheme
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import org.futo.inputmethod.keyboard.internal.KeyboardLayoutElement
import org.futo.inputmethod.keyboard.internal.KeyboardLayoutKind
import org.futo.inputmethod.keyboard.internal.KeyboardLayoutPage
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.databinding.FragmentThemeCustomizeBinding
import org.futo.inputmethod.latin.settings.LongPressKeySettings
import org.futo.inputmethod.latin.settings.Settings
import org.futo.inputmethod.latin.uix.BasicThemeProvider
import org.futo.inputmethod.latin.uix.wrapLightColorScheme
import org.futo.inputmethod.theme.model.KeyboardViewStyle
import org.futo.inputmethod.v2keyboard.KeyboardLayoutSetV2
import org.futo.inputmethod.v2keyboard.KeyboardLayoutSetV2Params
import org.futo.inputmethod.v2keyboard.RegularKeyboardSize


class ThemeCustomizeFragment : BaseMviFragment<
        FragmentThemeCustomizeBinding,
        ThemeCustomizeIntent,
        ThemeCustomizeState,
        ThemeCustomizeViewModel>() {

    private var themeConfig: ThemeConfig? = null

    companion object {
        private const val TAG = "ThemeCustomizeFragment"

        fun newInstance(uri: String): ThemeCustomizeFragment {
            return ThemeCustomizeFragment().apply {
                arguments = bundleOf("uri" to uri)
            }
        }
    }

    override val viewModel: ThemeCustomizeViewModel by viewModels()

    private var backgroundDrawable: Drawable? = null

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentThemeCustomizeBinding.inflate(inflater, container, false)

    override fun setupViews() {
        val uriStr = requireArguments().getString("uri")
        Log.d(TAG, "Received URI string: $uriStr")


        if (uriStr.isNullOrBlank()) {
            Log.e(TAG, "URI is null or blank, using fallback drawable")
            backgroundDrawable = ContextCompat.getDrawable(requireContext(), R.color.setup_text_action)
        } else {
            try {
                val uri = Uri.parse(uriStr)
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                backgroundDrawable = Drawable.createFromStream(inputStream, uri.toString())
                Log.d(TAG, "Drawable loaded successfully from URI")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load drawable from URI: ${e.message}")
                backgroundDrawable = ContextCompat.getDrawable(requireContext(), R.color.setup_text_action)
            }
        }

        viewModel.sendIntent(ThemeCustomizeIntent.UpdateBackground(backgroundDrawable))

        binding.seekBrightness.setOnSeekBarChangeListener(seekListener { value ->
            Log.d(TAG, "Brightness changed: $value")
//            viewModel.sendIntent(ThemeCustomizeIntent.UpdateBrightness(value / 100f))
            themeConfig?.let {
                it.backgroundAlpha = 1.0f - value / 100f
                binding.keyboardPreview.applyTheme(it)
            }
        })

        binding.seekOpacity.setOnSeekBarChangeListener(seekListener { value ->
            Log.d(TAG, "Key opacity changed: $value")
//            viewModel.sendIntent(ThemeCustomizeIntent.UpdateOpacity(value / 100f))
            themeConfig?.let {
                it.keyAlpha = 1.0f - value / 100f
                binding.keyboardPreview.applyTheme(it)
            }
        })

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, "Switch theme toggled: $isChecked")
            viewModel.sendIntent(ThemeCustomizeIntent.SwitchTheme(isChecked))
        }

        binding.btnCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.btnApply.setOnClickListener {
            Log.d(TAG, "Apply button clicked")

            themeConfig?.let { config ->
                val themeId = ThemePackageWriter.saveThemePackage(
                    context = requireContext(),
                    keyboardViewStyle = KeyboardViewStyle(
                        background = config.backgroundStyle,
                        keyBackground = config.keyBackground,
                        functionalKeyBackground = config.functionalKeyBackground,
                        spacebarBackground = config.spacebarBackground,
                        enterKeyBackground = config.enterKeyBackground,
                        numKeyBackground = config.numKeyBackground,
                        keyTextColor = config.keyTextColor,
                        keyTextColorPressed = config.keyTextColorPressed,
                        functionalKeyTextColor = config.functionalKeyTextColor
                    ),
                    backgroundDrawable = config.backgroundDrawable,
                    backgroundAlpha = config.backgroundAlpha,
                    keyAlpha = config.keyAlpha
                )

                if (themeId != null) {
                    Log.e(TAG, "save $themeId 主题 成功.")
//                    viewModel.sendIntent(ThemeCustomizeIntent.ApplyThemeId(themeId))
                } else {
                    Log.e(TAG, "Failed to save theme package.")
                }
            }
        }

    }

    override fun render(state: ThemeCustomizeState) {
        Log.d(TAG, "Rendering state: $state")

        val context = requireContext()
        themeConfig = ThemeConfig(
            backgroundDrawable = state.backgroundDrawable,
            backgroundAlpha = state.brightness,
            keyAlpha = state.keyOpacity
        )

        val colorScheme = wrapLightColorScheme(lightColorScheme()) // 或 wrapDarkColorScheme(...)
        val themeProvider = BasicThemeProvider(context, colorScheme)

        val settings = Settings.getInstance().current

        // 👇 手动构造一个默认的尺寸（宽度 = 屏宽，高度 = 280dp，padding 可忽略）
        val (defaultWidth, defaultHeight) = ThemeKeyboardSizeUtils.getDefaultKeyboardSize(requireContext()).run { width to height }

        val computedSize = RegularKeyboardSize(
            width = defaultWidth,
            height = defaultHeight,
            padding = Rect(0, 0, 0, 0)
        )

        val layoutSetName = "qwerty"

        val params = KeyboardLayoutSetV2Params(
            computedSize = computedSize,
            keyboardLayoutSet = layoutSetName,
            locale = settings.mLocale,
            editorInfo = EditorInfo(),
            numberRow = settings.mIsNumberRowEnabled,
            arrowRow = settings.mIsArrowRowEnabled,
            gap = 4f,
            bottomActionKey = if (settings.mShowsActionKey) settings.mActionKeyId else null,
            longPressKeySettings = LongPressKeySettings.load(context)
        )

        val layoutSet = KeyboardLayoutSetV2(context, params)
        val keyboard = layoutSet.getKeyboard(
            KeyboardLayoutElement(
                kind = KeyboardLayoutKind.Alphabet,
                page = KeyboardLayoutPage.Base
            )
        )

        binding.keyboardPreview.apply {
            setHardwareAcceleratedDrawingEnabled(true)
            setKeyboard(keyboard)
            background = themeConfig?.backgroundDrawable
            invalidateAllKeys()
        }
    }





    private fun seekListener(onChanged: (Int) -> Unit) =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) =
                onChanged(progress)

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        }
}
