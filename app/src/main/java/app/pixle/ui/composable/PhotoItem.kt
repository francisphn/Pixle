package app.pixle.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.model.entity.attempt.AttemptItem
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope

@Composable
fun PhotoItem(item: String, kind: String) {
    val gray = MaterialTheme.colorScheme.onBackground
    val color = remember(kind) {
        when (kind) {
            AttemptItem.KIND_EXACT -> Color(52, 211, 153).opacity(0.25f)
            AttemptItem.KIND_SIMILAR -> Color(251, 191, 36).opacity(0.25f)
            else -> gray.opacity(0.1f)
        }
    }
    Text(
        text = item,
        fontFamily = Manrope,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        modifier = Modifier
            .background(
                color,
                RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    )
}
