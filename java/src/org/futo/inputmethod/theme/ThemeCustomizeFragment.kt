package org.futo.inputmethod.theme

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.databinding.FragmentThemeCustomizeBinding

class ThemeCustomizeFragment : BaseMviFragment<
        FragmentThemeCustomizeBinding,
        ThemeCustomizeIntent,
        ThemeCustomizeState,
        ThemeCustomizeViewModel>() {

    companion object {
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
        val uri = Uri.parse(requireArguments().getString("uri"))

        // 将选中图片加载为 Drawable
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        backgroundDrawable = Drawable.createFromStream(inputStream, uri.toString())
            ?: ContextCompat.getDrawable(requireContext(), R.color.setup_text_action)

        viewModel.sendIntent(ThemeCustomizeIntent.UpdateBackground(backgroundDrawable))

        // 背景亮度 SeekBar
        binding.seekBrightness.setOnSeekBarChangeListener(seekListener {
            viewModel.sendIntent(ThemeCustomizeIntent.UpdateBrightness(it / 100f))
        })

        // 键帽透明度 SeekBar
        binding.seekOpacity.setOnSeekBarChangeListener(seekListener {
            viewModel.sendIntent(ThemeCustomizeIntent.UpdateOpacity(it / 100f))
        })

        // 明暗模式开关
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.sendIntent(ThemeCustomizeIntent.SwitchTheme(isChecked))
        }

        // 应用按钮
        binding.btnApply.setOnClickListener {
            viewModel.sendIntent(ThemeCustomizeIntent.Apply)
        }
    }

    override fun render(state: ThemeCustomizeState) {
        val themeConfig = ThemeConfig(
            backgroundDrawable = state.backgroundDrawable,
            backgroundAlpha = state.brightness,
            keyAlpha = state.keyOpacity
        )
        binding.keyboardPreview.applyTheme(themeConfig)
    }

    private fun seekListener(onChanged: (Int) -> Unit) =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) =
                onChanged(progress)

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        }
}
