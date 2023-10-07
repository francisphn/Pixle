package app.pixle.ui.composable.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.IncompleteCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.model.api.AttemptsHistory
import app.pixle.ui.composable.LoadingScreen
import app.pixle.ui.composable.RandomTextmojiMessage
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberQueryable
import app.pixle.ui.theme.Manrope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun History() {
    val (history, error) = rememberQueryable(AttemptsHistory)

    if (error != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            RandomTextmojiMessage(message = "Cannot load all attempts history")
        }
        return
    }

    if (history == null) {
        LoadingScreen()
        return
    }

    if (history.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            RandomTextmojiMessage(message = "You haven't played any game yet")
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize(),
    ) {
        items(history, key = { it.first }) { (date, attempts) ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val winningAttempt = attempts.firstOrNull { it.isWinningAttempt }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    winningAttempt?.let {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it.winningPhoto)
                                .build(),
                            contentDescription = date.toString(),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape((12 - (8 / 2)).dp))
                                .fillMaxSize()
                                .aspectRatio(1F)
                        )
                    } ?: run {
                        attempts
                            .takeLast(attempts.size.coerceAtMost(6))
                            .forEach { attempt ->
                                Row {
                                    attempt.attemptItems.forEach {
                                        WordleItem(it.kind)
                                    }
                                }
                            }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)),
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.opacity(0.75f)
                    )

                    winningAttempt?.let {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Winning",
                            modifier = Modifier.size(14.dp),
                            tint = Color(52, 211, 153).opacity(0.9f)
                        )
                    } ?: run {
                        Icon(
                            imageVector = Icons.Filled.IncompleteCircle,
                            contentDescription = "In progress",
                            modifier = Modifier.size(14.dp),
                            tint = Color(251, 191, 36).opacity(0.9f)
                        )
                    }
                }
            }
        }
    }
}