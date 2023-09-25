package app.pixle.model

import android.net.Uri


data class Snap(
    val uri: Uri?,
    val launchCamera: (Uri) -> Unit,
    val remove: () -> Unit,
) {
    /**
     * Launches the camera to take a photo and store it in the given [uri].
     * @see [androidx.activity.result.contract.ActivityResultContracts.TakePicture]
     */
    fun takePhoto(uri: Uri) {
        launchCamera(uri)
    }

    fun delete() {
        remove()
    }
}
