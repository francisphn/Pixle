package app.pixle.ui.composable.main

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.R
import app.pixle.lib.Utils
import app.pixle.lib.notEmptyOrNull
import app.pixle.lib.saveImageToGallery
import app.pixle.ui.composable.SmallButton
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavePhoto(image: Uri) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val today = remember(image) { Utils.utcDate() }
    val filename = remember(today) { "pixle-photo-${today}.jpeg" }

    val (isConfirming, setIsConfirming) = rememberSaveable { mutableStateOf(false) }
    val (isSaving, setIsSaving) = rememberSaveable { mutableStateOf(false) }


    SmallButton(
        modifier = Modifier.fillMaxWidth(),
        label = stringResource(R.string.save_photo),
        onClick = {
            setIsConfirming(true)
        }
    )

    if (isConfirming) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            sheetState = sheetState,
            onDismissRequest = { setIsConfirming(false) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.save_winning_photo),
                    fontFamily = Manrope,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                )

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
                        AsyncImage(
                            model = image.notEmptyOrNull(),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .size(60.dp)
                                .aspectRatio(1F),
                            contentDescription = "winning photo",
                            fallback = painterResource(R.drawable.image),
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                        ) {
                            Text(
                                text = stringResource(R.string.file_name, today.toString()),
                                fontFamily = Manrope,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                            )

                            Text(
                                text = stringResource(R.string.on_date,
                                    today.dayOfMonth,
                                    today.month.getDisplayName(TextStyle.SHORT, Locale.UK),
                                    today.year
                                ),
                                fontFamily = Manrope,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.opacity(0.6f)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable {
                            if (isSaving) return@clickable
                            scope.launch {
                                context.saveImageToGallery(image, filename)
                                setIsSaving(true)
                                delay(500)
                                setIsSaving(false)
                                sheetState.hide()
                                delay(100)
                                setIsConfirming(false)
                            }
                        }
                        .background(
                            color = MaterialTheme.colorScheme.primary.opacity(0.8f),
                            shape = CircleShape
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.save_photo),
                            fontFamily = Manrope,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}