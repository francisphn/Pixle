package app.pixle.ui.composable.camera

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.pixle.R
import app.pixle.asset.NEARBY_CONN_D_TAG
import app.pixle.database.AppPreferences
import app.pixle.lib.GameMode
import app.pixle.lib.asPayload
import app.pixle.lib.notEmptyOrNull
import app.pixle.model.api.AttemptsHistory
import app.pixle.model.api.AttemptsOfToday
import app.pixle.model.api.ConfirmAttempt
import app.pixle.model.api.Library
import app.pixle.model.api.SolutionOfToday
import app.pixle.model.entity.attempt.AtomicAttempt
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.model.entity.attempt.Attempt
import app.pixle.ui.composable.PhotoItem
import app.pixle.ui.composable.PolaroidFrame
import app.pixle.ui.composition.GameAnimation
import app.pixle.ui.composition.rememberConnectionInformation
import app.pixle.ui.composition.rememberGameAnimation
import app.pixle.ui.composition.rememberNearbyConnections
import app.pixle.ui.composition.rememberObjectDetection
import app.pixle.ui.state.rememberFusedLocation
import app.pixle.ui.state.rememberInvalidate
import app.pixle.ui.state.rememberMutable
import app.pixle.ui.state.rememberPreference
import app.pixle.ui.state.rememberQueryable
import app.pixle.ui.theme.Manrope
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.tensorflow.lite.support.image.TensorImage
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAnalysisSheet(
    uri: Uri?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    val fusedLocation = rememberFusedLocation()

    val connInfo = rememberConnectionInformation()
    val nearby = rememberNearbyConnections()

    val gameMode by rememberPreference(AppPreferences::getGameModePreference,
        initialValue = AppPreferences.DEFAULT_GAME_MODE
    )

    val (_, setAnimationState) = rememberGameAnimation()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val rotation = remember(uri) { if (Math.random() < 0.5f) 1.5f else -1.5f }

    val animatedRotation = animateFloatAsState(
        targetValue = uri?.let { rotation } ?: 0f,
        label = "rotation",
        animationSpec = tween(300, 100)
    )

    val objectDetector by rememberObjectDetection()

    val (goal, _) = rememberQueryable(SolutionOfToday)
    val (lib, _) = rememberQueryable(Library)
    val (attempts, _, _, _, invalidateToday) = rememberQueryable(AttemptsOfToday)
    val invalidateHistory = rememberInvalidate(AttemptsHistory)

    val (_, _, mutate) = rememberMutable(ConfirmAttempt) {
        onSuccess = { _, _, _ ->
            Log.d("pixle:debug", "created attempt")
            scope.launch {
                Log.d("pixle:debug", "invalidating")
                invalidateToday.invoke()
                invalidateHistory.invoke()
                Log.d("pixle:debug", "closing sheet")
                sheetState.hide()
            }.invokeOnCompletion {
                Log.d("pixle:debug", "removing sheet")
                onConfirm()
            }
        }
    }

    val (attempt, setAttempt) = rememberSaveable { mutableStateOf<Attempt?>(null) }


    LaunchedEffect(objectDetector, uri, lib, goal, attempt) {
        if (attempt != null) return@LaunchedEffect
        val detector = objectDetector ?: return@LaunchedEffect
        val image = uri ?: return@LaunchedEffect
        val knowledgeBase = lib ?: return@LaunchedEffect
        val items = goal?.solutionItems ?: return@LaunchedEffect

        val raw = ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.contentResolver, image)
        )
        val bitmap = raw.copy(Bitmap.Config.ARGB_8888, true)
        raw.recycle()

        val location = fusedLocation.lastLocationDisplayName()

        Log.d("pixle:analysis", "Photo taken at $location")

        val predictions = detector.detect(TensorImage.fromBitmap(bitmap))
        val givens = predictions
            .map { obj ->
                obj.categories.mapNotNull { category ->
                    knowledgeBase.find { it.name == category.label }
                }
            }
            .toMutableList()

        val currentAttempt = AtomicAttempt(
            uuid = UUID.randomUUID().toString(),
            solutionDate = goal.solution.date,
            winningPhoto = null,
            location = location
        )

        Log.d("pixle:analyse", "goal items: ${items.map { it.name }.joinToString(", ")}")

        val exacts = items.map { item ->
            val index = givens.indexOfFirst { given ->
                given.any { each -> each.name == item.name }
            }
            if (index == -1) return@map null
            val chosen = givens[index]
            givens.removeAt(index)
            return@map chosen.find { it.name == item.name }
        }

        Log.d("pixle:analyse", "exacts: ${exacts.map { it?.icon }.joinToString(", ")}")

        val similars = items.map { item ->
            val index = givens.indexOfFirst { given ->
                given.any { each -> each.category == item.category }
            }
            if (index == -1) return@map null
            val chosen = givens[index]
            givens.removeAt(index)
            return@map chosen.find { it.category == item.category }
        }

        Log.d("pixle:analyse", "similar: ${similars.map { it?.icon }.joinToString(", ")}")

        val result = items.mapIndexed { idx, _ ->
            val exact = exacts[idx]

            if (exact != null) {
                return@mapIndexed AtomicAttemptItem(
                    icon = exact.icon,
                    attemptUuid = currentAttempt.uuid,
                    positionInAttempt = idx.toLong(),
                    kind = AtomicAttemptItem.KIND_EXACT
                )
            }

            val similar = similars[idx]

            if (similar != null) {
                return@mapIndexed AtomicAttemptItem(
                    icon = similar.icon,
                    attemptUuid = currentAttempt.uuid,
                    positionInAttempt = idx.toLong(),
                    kind = AtomicAttemptItem.KIND_SIMILAR
                )
            }

            val unmatched = givens.removeFirstOrNull()?.firstOrNull()

            if (unmatched != null) {
                return@mapIndexed AtomicAttemptItem(
                    icon = unmatched.icon,
                    attemptUuid = currentAttempt.uuid,
                    positionInAttempt = idx.toLong(),
                    kind = AtomicAttemptItem.KIND_NONE
                )
            }

            return@mapIndexed AtomicAttemptItem(
                icon = "",
                attemptUuid = currentAttempt.uuid,
                positionInAttempt = idx.toLong(),
                kind = AtomicAttemptItem.KIND_NONE
            )
        }

        bitmap.recycle()
        Log.d("pixle:analyse", "result: ${result.map { it.icon }.joinToString(", ")}")

        setAttempt(Attempt(currentAttempt, result))
    }

    if (uri != null) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .zIndex(40f),
            sheetState = sheetState,
            onDismissRequest = { onDismiss() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 44.dp),
                ) {
                    Text(
                        text = stringResource(R.string.eval_photo),
                        fontFamily = Manrope,
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                PolaroidFrame(
                    modifier = Modifier
                        .rotate(animatedRotation.value)
                ) {
                    AsyncImage(
                        model = uri.notEmptyOrNull(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(250.dp)
                            .height(250.dp),
                        contentDescription = null,
                        fallback = painterResource(R.drawable.image),
                    )
                }

                Box {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = attempt == null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 44.dp, vertical = 24.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .height(24.dp)
                            )
                        }
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = attempt != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 44.dp, vertical = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                10.dp,
                                Alignment.CenterHorizontally
                            ),
                        ) {
                            items(
                                items = attempt?.attemptItems
                                    ?: listOf(),
                                key = { it.positionInAttempt }
                            ) {
                                PhotoItem(
                                    item = it.icon,
                                    kind = AtomicAttemptItem.KIND_NONE,
                                )
                            }
                        }
                    }
                }


                Row(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 56.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 44.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                ) {
                    ElevatedButton(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    setAttempt(null)
                                    onDismiss()
                                }
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontFamily = Manrope,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            val concluded = (attempts != null && (attempts.any { it.isWinningAttempt } || (attempts.size >= 6 && gameMode == GameMode.Hard)))
                            if (concluded) {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        setAttempt(null)
                                        onDismiss()
                                    }
                                }
                                return@Button
                            }

                            val confirmedAttempt = attempt ?: return@Button

                            scope.launch {
                                mutate.invoke(Triple(confirmedAttempt, uri, gameMode))

                                Log.d(NEARBY_CONN_D_TAG, "Sending attempt to other device...")

                                connInfo.endpoints.otherEndpointReadableId?.let {
                                    nearby.sendPayload(
                                        it,
                                        "ATTEMPT|||${Json.encodeToString(confirmedAttempt)}".asPayload()
                                    )
                                }

                            }.invokeOnCompletion {
                                val win = confirmedAttempt.attemptItems.all { it.kind == AtomicAttemptItem.KIND_EXACT }
                                setAnimationState(if (win) GameAnimation.State.WIN else GameAnimation.State.ATTEMPT)
                            }

                        }
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            fontFamily = Manrope,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

