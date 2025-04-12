package org.futo.inputmethod.theme

import android.net.Uri

sealed class PhotoPickerIntent {
    data class PhotoSelected(val uri: Uri) : PhotoPickerIntent()
}

data class PhotoPickerState(
    val selectedUri: Uri? = null,
    val navigateToEditor: Boolean = false
)

class PhotoPickerViewModel : BaseMviViewModel<PhotoPickerIntent, PhotoPickerState>(
    PhotoPickerState()
) {
    override fun handleIntent(intent: PhotoPickerIntent) {
        when (intent) {
            is PhotoPickerIntent.PhotoSelected -> {
                setState { it.copy(selectedUri = intent.uri, navigateToEditor = true) }
            }
        }
    }
}
