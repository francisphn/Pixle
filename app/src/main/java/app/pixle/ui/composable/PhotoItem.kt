package app.pixle.ui.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope

@Composable
fun PhotoItem(
    item: String? = null,
    kind: String = AtomicAttemptItem.KIND_NONE,
    animate: Pair<Boolean, Int> = Pair(false, 0),
) {
    val (shouldAnimate, delay) = animate
    val gray = MaterialTheme.colorScheme.onBackground
    val color = remember(kind) {
        when (kind) {
            AtomicAttemptItem.KIND_EXACT -> Color(52, 211, 153).opacity(0.25f)
            AtomicAttemptItem.KIND_SIMILAR -> Color(251, 191, 36).opacity(0.25f)
            AtomicAttemptItem.KIND_NONE -> gray.opacity(0.1f)
            else -> gray.opacity(0.15f)
        }
    }

    val rotation by animateFloatAsState(
        targetValue = if (!shouldAnimate) 180f else 0f,
        animationSpec = tween(700, delay),
        label = "rotation"
    )

    val animateFront by animateFloatAsState(
        targetValue = if (shouldAnimate) 1f else 0f,
        animationSpec = tween(700, delay),
        label = "animateFront"
    )

    val animateBack by animateFloatAsState(
        targetValue = if (!shouldAnimate) 1f else 0f,
        animationSpec = tween(700, delay),
        label = "animateBack"
    )

    val animateColor by animateColorAsState(
        targetValue = if (!shouldAnimate) color else gray.opacity(0.1f),
        animationSpec = tween(700, delay),
        label = "animateColor"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = if (!shouldAnimate) animateBack else animateFront
                rotationY = rotation
            }
            .background(
                animateColor, RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
            .size(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        item?.let {
            Text(
                text = it,
                fontFamily = Manrope,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
            )
        }
    }

}



