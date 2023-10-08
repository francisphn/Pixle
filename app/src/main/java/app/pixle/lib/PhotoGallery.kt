package app.pixle.lib

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

suspend fun Context.saveImageToGallery(uri: Uri, filename: String): Boolean = suspendCancellableCoroutine { cont ->
    MediaStore.Images.Media.insertImage(
        contentResolver,
        uri.path?.let { File(it).absolutePath },
        filename,
        "Winning photo from Pixle"
    )
    cont.resume(true)
}
