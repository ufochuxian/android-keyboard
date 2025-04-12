package org.futo.inputmethod.theme

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.databinding.FragmentPhotoPickerBinding

class PhotoPickerFragment : BaseMviFragment<
        FragmentPhotoPickerBinding,
        PhotoPickerIntent,
        PhotoPickerState,
        PhotoPickerViewModel>() {

    companion object {
        fun newInstance(): PhotoPickerFragment = PhotoPickerFragment()
    }

    override val viewModel: PhotoPickerViewModel by viewModels()

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.sendIntent(PhotoPickerIntent.PhotoSelected(it))
        }
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPhotoPickerBinding.inflate(inflater, container, false)

    override fun setupViews() {
        binding.btnSelectPhoto.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }
    }

    override fun render(state: PhotoPickerState) {
        if (state.navigateToEditor && state.selectedUri != null) {
            parentFragmentManager.beginTransaction()
                .add(R.id.fragment_container, PhotoEditFragment.newInstance(state.selectedUri.toString())) // ✅ FIXED
                .addToBackStack(null)
                .commit()
        }
    }

}
