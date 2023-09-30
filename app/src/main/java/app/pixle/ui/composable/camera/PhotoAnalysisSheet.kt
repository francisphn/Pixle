package app.pixle.ui.composable.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.pixle.model.api.Goal
import app.pixle.model.api.Library
import app.pixle.model.entity.attempt.Attempt
import app.pixle.model.entity.attempt.AttemptItem
import app.pixle.model.entity.attempt.AttemptWithItems
import app.pixle.ui.composable.PhotoItem
import app.pixle.ui.composable.PolaroidFrame
import app.pixle.ui.state.ObjectDetectionModel
import app.pixle.ui.state.rememberObjectDetector
import app.pixle.ui.state.rememberQueryable
import app.pixle.ui.theme.Manrope
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAnalysisSheet(
    bitmap: Bitmap?,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val rotation = remember(bitmap) { if (Math.random() < 0.5f) 1.5f else -1.5f }
    val animatedRotation = animateFloatAsState(
        targetValue = bitmap?.let { rotation } ?: 0f,
        label = "rotation",
        animationSpec = tween(300, 100)
    )

    val (goal, _) = rememberQueryable(Goal)
    val (lib, _) = rememberQueryable(Library)
    val objectDetector = rememberObjectDetector(model = ObjectDetectionModel.EDL1)

    val (attempt, setAttempt) = remember { mutableStateOf<AttemptWithItems?>(null) }


    LaunchedEffect(objectDetector, bitmap, lib, goal, attempt) {
        if (attempt != null) return@LaunchedEffect

        val detector = objectDetector ?: return@LaunchedEffect
        val image = bitmap ?: return@LaunchedEffect
        val knowledgeBase = lib ?: return@LaunchedEffect
        val items = goal?.items ?: return@LaunchedEffect

        val predictions = detector.detect(TensorImage.fromBitmap(image))
        val givens = predictions
            .map { obj ->
                obj.categories.mapNotNull { category ->
                    knowledgeBase.find { it.name == category.label }
                }
            }
            .toMutableList()

        val currentAttempt = Attempt(
            uuid = UUID.randomUUID().toString(),
            solutionDate = goal.day,
        )

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
                return@mapIndexed AttemptItem(
                    emoji = exact.icon,
                    attemptUuid = currentAttempt.uuid,
                    positionInAttempt = idx.toLong(),
                    kind = AttemptItem.KIND_EXACT
                )
            }

            val similar = similars[idx]

            if (similar != null) {
                return@mapIndexed AttemptItem(
                    emoji = similar.icon,
                    attemptUuid = currentAttempt.uuid,
                    positionInAttempt = idx.toLong(),
                    kind = AttemptItem.KIND_SIMILAR
                )
            }

            val unmatched = givens.removeFirstOrNull()?.firstOrNull()

            if (unmatched != null) {
                return@mapIndexed AttemptItem(
                    emoji = unmatched.icon,
                    attemptUuid = currentAttempt.uuid,
                    positionInAttempt = idx.toLong(),
                    kind = AttemptItem.KIND_NONE
                )
            }

            return@mapIndexed AttemptItem(
                emoji = "",
                attemptUuid = currentAttempt.uuid,
                positionInAttempt = idx.toLong(),
                kind = AttemptItem.KIND_NONE
            )
        }

        Log.d("pixle:analyse", "result: ${result.map { it.emoji }.joinToString(", ")}")

        setAttempt(AttemptWithItems(currentAttempt, result))
    }

    if (bitmap != null) {
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
                        text = "Evaluating photo",
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
                        model = bitmap,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(250.dp)
                            .height(250.dp),
                        contentDescription = null,
                    )
                }

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
                            item = it.emoji,
                            kind = it.kind,
                        )
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
                            text = "Cancel",
                            fontFamily = Manrope,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = { /*TODO*/ }
                    ) {
                        Text(
                            text = "Confirm",
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

