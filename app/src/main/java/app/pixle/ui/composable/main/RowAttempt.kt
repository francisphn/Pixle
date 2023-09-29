package app.pixle.ui.composable.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.pixle.model.entity.attempt.AttemptItem
import app.pixle.ui.composable.PhotoItem

@Composable
fun RowAttempt(
    items: List<AttemptItem>
) {
    Row(
        modifier = Modifier.offset(x = (-4).dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.onBackground, CircleShape
                )
                .size(8.dp)
        )

        items.forEach {
            PhotoItem(item = it.emoji, kind = it.kind)
        }
    }
}