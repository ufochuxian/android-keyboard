package org.futo.inputmethod.theme

import android.net.Uri

sealed class PhotoEditIntent {
    data class OnNextClicked(val uri: Uri) : PhotoEditIntent()
}

data class PhotoEditState(
    val editedUri: Uri? = null,
    val navigateToCustomize: Boolean = false
)

class PhotoEditViewModel : BaseMviViewModel<PhotoEditIntent, PhotoEditState>(
    PhotoEditState()
) {
    override fun handleIntent(intent: PhotoEditIntent) {
        when (intent) {
            is PhotoEditIntent.OnNextClicked -> {
                setState { it.copy(editedUri = intent.uri, navigateToCustomize = true) }
            }
        }
    }
}
