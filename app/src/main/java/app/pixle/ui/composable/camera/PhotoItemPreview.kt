package app.pixle.ui.composable.camera

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope

@Composable
fun PhotoItemPreview(
    item: String? = null,
    kind: String = AtomicAttemptItem.KIND_NONE,
    delay: Int = 0,
) {
    val gray = MaterialTheme.colorScheme.onBackground
    val color = remember(kind) {
        when (kind) {
            AtomicAttemptItem.KIND_EXACT -> Color(52, 211, 153).opacity(0.25f)
            AtomicAttemptItem.KIND_SIMILAR -> Color(251, 191, 36).opacity(0.25f)
            AtomicAttemptItem.KIND_NONE -> gray.opacity(0.1f)
            else -> gray.opacity(0.15f)
        }
    }

    val (isLoaded, setIsLoaded) = remember { mutableStateOf(false) }

    val animatedY by animateDpAsState(
        targetValue = if (isLoaded) 0.dp else 10.dp,
        animationSpec = tween(300, delay),
        label = "offset-y"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isLoaded) 0.75f else 0f,
        animationSpec = tween(300, delay),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        setIsLoaded(true)
    }

    Box(
        modifier = Modifier
            .offset(y = animatedY)
            .alpha(animatedAlpha)
            .border(2.dp, color, RoundedCornerShape(10.dp))
            .background(
                Color.White.opacity(0.75f), RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
            .size(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        item?.let {
            Text(
                text = it,
                fontFamily = Manrope,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(animatedAlpha)
            )
        }
    }

}



