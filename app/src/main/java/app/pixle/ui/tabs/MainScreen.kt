package app.pixle.ui.tabs

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.R
import app.pixle.database.PixleDatabase
import app.pixle.model.api.SolutionOfToday
import app.pixle.model.entity.attempt.Attempt
import app.pixle.ui.composable.LoadingScreen
import app.pixle.ui.composable.main.MissingRowAttempt
import app.pixle.ui.composable.main.NoWinningPhoto
import app.pixle.ui.composable.main.RowAttempt
import app.pixle.ui.modifier.leftBorder
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberQueryable
import app.pixle.ui.theme.Manrope
import app.pixle.ui.theme.rarityColour
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun MainScreen() {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val attemptRepository = PixleDatabase
        .getInstance(context)
        .attemptRepository()

    val (goal, _) = rememberQueryable(SolutionOfToday) {
        onError = { err, _, _ ->
            Log.d("pixle:main", "got an error: $err")
        }
    }
    val difficultyColour = remember(goal) { goal?.difficulty?.let { rarityColour(it) } }
    var attempts by remember { mutableStateOf(listOf<Attempt>()) }

    LaunchedEffect(Unit) {
        attempts = attemptRepository.getAttemptsOfToday()

        return@LaunchedEffect
    }

    if (goal == null || difficultyColour == null) {
        LoadingScreen()
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {


        // Welcome message
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Hi, player",
                    fontFamily = Manrope,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.alpha(0.5f)
                )
                Text(
                    text = "Welcome Back!",
                    fontFamily = Manrope,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Main content
        item {
            Column(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.dp, color = difficultyColour, shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 20.dp),
            ) {

                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                            .padding(8.dp)
                    ) {
                        Image(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(R.drawable.bling),
                            contentDescription = "bling",
                            colorFilter = ColorFilter.tint(difficultyColour)
                        )
                    }

                    Text(
                        text = "Today's goal",
                        fontFamily = Manrope,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Column {
                        Text(
                            text = "${LocalDate.now().dayOfMonth}",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "${
                                LocalDate.now().month.getDisplayName(
                                    TextStyle.SHORT, Locale.UK
                                )
                            } ${LocalDate.now().year}",
                            fontFamily = Manrope,
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            modifier = Modifier.alpha(0.75f)
                        )
                    }
                }

                // Info
                Text(
                    text = "${goal.solutionItems.size} items • ${goal.difficulty} difficulty",
                    fontFamily = Manrope,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 8.dp),
                )

                // Attempts
                Column(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .leftBorder(
                                1.dp,
                                MaterialTheme.colorScheme.onBackground.opacity(0.3f)
                            ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        attempts.forEach {
                            RowAttempt(
                                items = it
                            )
                        }

                        (0 until (6 - attempts.size)).forEach { _ ->
                            MissingRowAttempt(size = goal.solutionItems.size)
                        }
                    }
                }

                // No winning photo
                NoWinningPhoto()
            }
        }
    }
}