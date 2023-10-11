package app.pixle.ui.composable.profile

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import app.pixle.R
import app.pixle.ui.composable.RandomTextmojiMessage
import app.pixle.ui.composable.SmallButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile() {
    val (isEditing, setIsEditing) = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    SmallButton(
        label = stringResource(R.string.edit_profile),
        onClick = {
            setIsEditing(true)
        }
    )

    if (isEditing) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .zIndex(40f),
            sheetState = sheetState,
            onDismissRequest = { setIsEditing(false) },
        ) {
            RandomTextmojiMessage(message = "We haven't implemented editing yet", modifier = Modifier.fillMaxSize(),)
        }
    }
}