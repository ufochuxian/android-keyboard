package org.futo.inputmethod.theme

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.databinding.FragmentPhotoEditBinding

class PhotoEditFragment : BaseMviFragment<
        FragmentPhotoEditBinding,
        PhotoEditIntent,
        PhotoEditState,
        PhotoEditViewModel>() {

    companion object {
        fun newInstance(uri: String): PhotoEditFragment {
            return PhotoEditFragment().apply {
                arguments = bundleOf("uri" to uri)
            }
        }
    }

    override val viewModel: PhotoEditViewModel by viewModels()

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPhotoEditBinding.inflate(inflater, container, false)

    override fun setupViews() {
        val uri = Uri.parse(requireArguments().getString("uri"))
        binding.photoView.setImageURI(uri)

        binding.btnNext.setOnClickListener {
            viewModel.sendIntent(PhotoEditIntent.OnNextClicked(uri))
        }
    }

    override fun render(state: PhotoEditState) {
        if (state.navigateToCustomize && state.editedUri != null) {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    ThemeCustomizeFragment.newInstance(state.editedUri.toString())
                )
                .addToBackStack(null)
                .commit()
        }
    }
}
