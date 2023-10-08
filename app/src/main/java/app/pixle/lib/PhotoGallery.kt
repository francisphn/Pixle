package app.pixle.lib

import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class GalleryObserver(
    private val context: Context,
    private val imageUri: Uri,
    private val callback: () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {
    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        if (uri == imageUri) {
            callback.invoke()
            context.contentResolver.unregisterContentObserver(this)
        }
    }
}

suspend fun Context.saveImageToGallery(uri: Uri, filename: String): Boolean = suspendCancellableCoroutine { cont ->
    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
    }

    val imageUri = contentResolver
        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    imageUri?.let {
        contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            bitmap.recycle()

            contentResolver.registerContentObserver(
                imageUri,
                false,
                GalleryObserver(this, imageUri) {
                    cont.resume(true)
                }
            )
        }
    } ?: run {
        cont.resume(false)
    }
}
