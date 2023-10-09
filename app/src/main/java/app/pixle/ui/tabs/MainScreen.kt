package app.pixle.ui.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.R
import app.pixle.lib.Utils
import app.pixle.model.api.AttemptsOfToday
import app.pixle.model.api.SolutionOfToday
import app.pixle.ui.composable.LoadingScreen
import app.pixle.ui.composable.RandomTextmojiMessage
import app.pixle.ui.composable.main.Celebration
import app.pixle.ui.composable.main.Game
import app.pixle.ui.composable.main.Hint
import app.pixle.ui.composable.main.MissingRowAttempt
import app.pixle.ui.composable.main.WinningPhoto
import app.pixle.ui.composable.main.RowAttempt
import app.pixle.ui.composition.GameAnimation
import app.pixle.ui.composition.LocalGameAnimation
import app.pixle.ui.composition.rememberGameAnimation
import app.pixle.ui.modifier.leftBorder
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberPreferences
import app.pixle.ui.state.rememberQueryable
import app.pixle.ui.theme.Manrope
import app.pixle.ui.theme.rarityColour
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun MainScreen() {
    val (goal, goalError) = rememberQueryable(SolutionOfToday)
    val (attempts, attemptsError) = rememberQueryable(AttemptsOfToday)

    val today = remember(goal) { Utils.utcDate() }
    val difficultyColour = remember(goal) { goal?.difficulty?.let { rarityColour(it) } }

    val preferences = rememberPreferences()
    val playerName by preferences.getPlayerName.collectAsState(initial = stringResource(R.string.initial_player_name))

    AnimatedVisibility(
        visible = goalError != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            RandomTextmojiMessage(message = stringResource(R.string.no_internet))
        }
    }

    AnimatedVisibility(
        visible = goalError == null && attemptsError != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            RandomTextmojiMessage(message = stringResource(R.string.load_attempts_error))
        }
    }

    AnimatedVisibility(
        visible = goalError == null && attemptsError == null && (goal == null || attempts == null || difficultyColour == null),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LoadingScreen()
    }

    AnimatedVisibility(
        visible = goalError == null && attemptsError == null && goal != null && attempts != null && difficultyColour != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {

        if (goal == null || attempts == null || difficultyColour == null) {
            return@AnimatedVisibility
        }

        Celebration(attempts = attempts) {
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
                            text = "Hi, $playerName",
                            fontFamily = Manrope,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.alpha(0.5f)
                        )
                        Text(
                            text = stringResource(R.string.welcome),
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
                                width = 1.dp,
                                color = difficultyColour,
                                shape = RoundedCornerShape(12.dp)
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
                                text = stringResource(R.string.goal),
                                fontFamily = Manrope,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Column {
                                Text(
                                    text = "${if (today.dayOfMonth < 10) "0" else ""}${today.dayOfMonth}",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = today.month.getDisplayName(TextStyle.SHORT, Locale.UK) + " " + today.year.toString(),
                                    fontFamily = Manrope,
                                    fontSize = 10.sp,
                                    lineHeight = 10.sp,
                                    modifier = Modifier.alpha(0.75f)
                                )
                            }
                        }

                        // Info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.game_desc, goal.solutionItems.size, goal.difficulty),
                                fontFamily = Manrope,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Medium,
                            )

                            // Hint
                            Hint(
                                goal = goal,
                                attempts = attempts,
                                color = difficultyColour
                            )
                        }


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
                                Game(attempts = attempts, goal = goal)
                            }
                        }

                        // No winning photo
                        WinningPhoto(
                            attempts = attempts
                        )
                    }
                }
            }
        }
    }
}