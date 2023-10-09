package app.pixle.ui.composable.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.twotone.AutoDelete
import androidx.compose.material.icons.twotone.SentimentVeryDissatisfied
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import app.pixle.R
import app.pixle.model.api.AttemptsHistory
import app.pixle.model.api.AttemptsOfToday
import app.pixle.model.api.Forfeit
import app.pixle.ui.composable.SmallButton
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberInvalidate
import app.pixle.ui.state.rememberMutable
import app.pixle.ui.theme.Manrope
import kotlinx.coroutines.launch

@Composable
fun ForfeitToday() {
    val isDarkTheme = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()
    val (isOpen, setIsOpen) = remember { mutableStateOf(false) }

    val invalidateToday = rememberInvalidate(AttemptsOfToday)
    val invalidateHistory = rememberInvalidate(AttemptsHistory)
    val (_, _, forfeit) = rememberMutable(Forfeit) {
        onSuccess = { _, _, _ ->
            scope.launch {
                invalidateToday.invoke()
                invalidateHistory.invoke()
            }.invokeOnCompletion {
                setIsOpen(false)
            }
        }
    }

    IconButton(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.error.opacity(0.8f),
                CircleShape
            )
            .size(30.dp),
        onClick = { setIsOpen(true) },
    ) {
        Icon(
            Icons.Filled.DeleteSweep,
            contentDescription = "reset",
            tint = MaterialTheme.colorScheme.onError,
            modifier = Modifier
                .size(18.dp)
        )
    }

    AnimatedVisibility(visible = isOpen) {
        AlertDialog(
            onDismissRequest = { setIsOpen(false) },
            title = {
                Text(
                    text = stringResource(R.string.forfeit),
                    fontFamily = Manrope,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            scope.launch {
                                forfeit.invoke(Unit)
                            }
                        }
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer.opacity(0.8f),
                            shape = CircleShape
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.confirm),
                        fontFamily = Manrope,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            setIsOpen(false)
                        }
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.opacity(0.8f),
                            shape = CircleShape
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontFamily = Manrope,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            },
            containerColor = if (isDarkTheme) Color(20, 20, 20) else Color.White,
        )
    }
}