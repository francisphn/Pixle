package app.pixle.ui.composable

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.pixle.model.Snap

@Composable
fun SnapProvider(
    content: @Composable Snap.() -> Unit
) {
    val (photoUri, setPhotoUri) = remember { mutableStateOf<Uri?>(null) }
    val (tempUri, setTempUri) = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        if (it) {
            setPhotoUri(tempUri)
            setTempUri(null)
        }
    }

    val snap = remember(photoUri) {
        Snap(uri = photoUri, launchCamera = {
            setTempUri(it)
            launcher.launch(it)
        }, remove = {
            setPhotoUri(null)
        })
    }

    snap.content()
}