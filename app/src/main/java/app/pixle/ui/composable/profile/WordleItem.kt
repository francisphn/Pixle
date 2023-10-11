package app.pixle.ui.composable.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.ui.modifier.opacity

@Composable
fun WordleItem(kind: String? = null) {
    val gray = MaterialTheme.colorScheme.onBackground
    val color = remember(kind) {
        when (kind) {
            AtomicAttemptItem.KIND_EXACT -> Color(52, 211, 153).opacity(0.5f)
            AtomicAttemptItem.KIND_SIMILAR -> Color(251, 191, 36).opacity(0.5f)
            AtomicAttemptItem.KIND_NONE -> gray.opacity(0.2f)
            else -> gray.opacity(0.4f)
        }
    }

    Box(
        modifier = Modifier
            .padding(1.dp)
            .background(
                color, RectangleShape
            )
            .size(20.dp)
    )
}



