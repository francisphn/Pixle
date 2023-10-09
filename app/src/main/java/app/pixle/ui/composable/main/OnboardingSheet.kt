package app.pixle.ui.composable.main

import android.icu.util.TimeZone
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.datastore.dataStore
import app.pixle.R
import app.pixle.database.AppPreferences
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.ui.composable.PhotoItem
import app.pixle.ui.composable.PolaroidFrame
import app.pixle.ui.composable.RandomTextmojiMessage
import app.pixle.ui.composable.TextmojiSize
import app.pixle.ui.composable.TextmojiTone
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date
import kotlin.math.abs


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingSheet() {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val appPreferences = AppPreferences.getInstance(context)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var shouldLaunchOnboarding by remember { mutableStateOf(true) }

    val offsetHour = LocalDateTime.now().hour - LocalDateTime.now(Clock.systemUTC()).hour
    val offsetMinute = LocalDateTime.now().minute - LocalDateTime.now(Clock.systemUTC()).minute

    val localTimeOfReset = "${DecimalFormat("00").format(abs(offsetHour).minus(12))}:" +
            "${DecimalFormat("00").format(abs(offsetMinute))} " +
            if (offsetHour < 0 || offsetHour > 12) " pm" else " am"

    val animatedRotation = animateFloatAsState(
        targetValue = 1.5f,
        label = "rotation",
        animationSpec = tween(300, 100)
    )

    LaunchedEffect(Unit) {
        delay(1000)

        appPreferences
            .shouldLaunchOnboardingPane
            .flowOn(Dispatchers.IO)
            .collect {
                shouldLaunchOnboarding = it

                Log.d("pixle:debug", "Launching onboarding? $shouldLaunchOnboarding")
            }
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {

    }

    if (!shouldLaunchOnboarding) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .zIndex(40f),
            sheetState = sheetState,
            onDismissRequest =
            {
                scope.launch {
                    appPreferences.dismissOnboardingPane()

                    permissionsLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            },
        ) {

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(30.dp)
            ) {
                Text(
                    text = stringResource(R.string.welcome_to_pixle),
                    fontFamily = Manrope,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                Row(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PolaroidFrame(Modifier.rotate(animatedRotation.value)) {
                        Image(
                            painter = painterResource(id = R.drawable.demo_photo),
                            contentScale = ContentScale.Crop,
                            contentDescription = "Demo photo of a bird",
                            modifier = Modifier.size(100.dp, 100.dp)
                        )
                    }

                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "")

                    PhotoItem(item = stringResource(R.string.bird_icon), AtomicAttemptItem.KIND_NONE)
                }



                Text(
                    text = stringResource(R.string.how_to_play),

                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.onboarding_desc, localTimeOfReset),
                    modifier = Modifier.padding(bottom = 20.dp),
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal
                )

                Text(
                    text = stringResource(R.string.notifications),

                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.notifications_desc),

                    modifier = Modifier.padding(bottom = 30.dp),

                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal
                )

                Box(modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onTertiaryContainer)
                    .clickable {
                        scope.launch {
                            sheetState.hide()

                            appPreferences.dismissOnboardingPane()

                            permissionsLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.opacity(0.25f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.continue_text),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface,
                    )
                }
            }
        }
    }
}