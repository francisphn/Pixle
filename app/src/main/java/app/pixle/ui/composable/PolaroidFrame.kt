package app.pixle.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PolaroidFrame(
    modifier: Modifier = Modifier,
    padding: Pair<Dp, Dp> = Pair(20.dp, 48.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(0.dp))
            .padding(
                top = padding.first,
                start = padding.first,
                end = padding.first,
                bottom = padding.second
            ),
        content = content
    )
}