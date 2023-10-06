package app.pixle.ui.composable.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.pixle.ui.composable.RandomTextmojiMessage
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile() {
    val (isEditing, setIsEditing) = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier
        .clip(CircleShape)
        .clickable {
            setIsEditing(true)
        }
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onBackground.opacity(0.25f),
            shape = CircleShape
        )
        .padding(horizontal = 14.dp, vertical = 6.dp)) {
        Text(
            text = "Edit profile",
            fontFamily = Manrope,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }


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