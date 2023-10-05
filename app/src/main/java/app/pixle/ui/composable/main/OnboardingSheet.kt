package app.pixle.ui.composable.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.pixle.database.AppPreferences
import app.pixle.ui.composable.RandomTextmojiMessage
import app.pixle.ui.composable.TextmojiSize
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionSheet() {

    val context = LocalContext.current

    val appPreferences = AppPreferences.getInstance(context)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var hasPreviouslyAskedForNotificationPermission = true

    LaunchedEffect(Unit) {
        delay(1000)

        appPreferences
            .hasPreviouslyAskedForNotificationPermission
            .flowOn(Dispatchers.IO)
            .collect {
                hasPreviouslyAskedForNotificationPermission = it

                Log.d("pixle:debug", "Previously asked for permission? $hasPreviouslyAskedForNotificationPermission")
            }
    }

    if (!hasPreviouslyAskedForNotificationPermission) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .zIndex(40f),
            sheetState = sheetState,
            onDismissRequest = { hasPreviouslyAskedForNotificationPermission = true },
        ) {

            Column {
                RandomTextmojiMessage(message = "Welcome to Pixle", size = TextmojiSize.SMALL)
                Text(text = "Welcome to Pixle!")
                Text(text = "Pixle uses notifications to remind you to touch grass daily.")

                Box(modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onTertiaryContainer)
                    .clickable {

                    }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.opacity(0.25f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Text(
                        text = "Allow notifications",
                        fontFamily = Manrope,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface,
                    )
                }

                Box(modifier = Modifier
                    .clip(CircleShape)
                    .clickable {

                    }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.opacity(0.25f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Text(
                        text = "No thanks",
                        fontFamily = Manrope,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

            }

        }
    }
}