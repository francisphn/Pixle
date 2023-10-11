package app.pixle.ui.composable.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.twotone.FiberManualRecord
import androidx.compose.material.icons.twotone.FiberSmartRecord
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.R
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartPreviewToggle(
    isDetectingMotion: Boolean,
    onEnable: () -> Unit,
    onDisable: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val color = remember { Color(45, 212, 191) }
    val icon = remember(isDetectingMotion) { if (isDetectingMotion) Icons.TwoTone.FiberSmartRecord else Icons.TwoTone.FiberManualRecord }

    val (isConfirming, setIsConfirming) = rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = {
            if (isDetectingMotion) {
                onDisable()
            } else {
                setIsConfirming(true)
            }
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "auto-detect",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
        )
    }

    if (isConfirming) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp),
            sheetState = sheetState,
            onDismissRequest = { setIsConfirming(false) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.FiberSmartRecord,
                        contentDescription = "smart-preview",
                        modifier = Modifier.size(40.dp),
                        tint = color
                    )
                    Text(
                        text = stringResource(R.string.smart_preview),
                        fontFamily = Manrope,
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = stringResource(R.string.smart_preview_desc),
                        fontFamily = Manrope,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }



                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "warning",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Yellow
                        )
                        Text(
                            text = stringResource(R.string.smart_preview_warning),
                            fontFamily = Manrope,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable {
                            scope
                                .launch {
                                    sheetState.hide()
                                }
                                .invokeOnCompletion {
                                    setIsConfirming(false)
                                    onEnable()
                                }
                        }
                        .background(
                            color = MaterialTheme.colorScheme.onBackground.opacity(0.8f),
                            shape = CircleShape
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                        Text(
                            text = stringResource(R.string.enable),
                            fontFamily = Manrope,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.background,
                        )
                }
            }
        }
    }
}