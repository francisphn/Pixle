package app.pixle.ui.composable.main

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.R
import app.pixle.database.AppPreferences
import app.pixle.lib.GameMode
import app.pixle.model.entity.attempt.Attempt
import app.pixle.model.entity.solution.Solution
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberPreference
import app.pixle.ui.theme.Manrope



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Hint(goal: Solution, attempts: List<Attempt>, color: Color) {
    val runesChar = stringResource(R.string.runes_characters).split("")
    val rune1 = stringResource(R.string.rune_1)
    val rune2 = stringResource(R.string.rune_2)
    val rune3 = stringResource(R.string.rune_3)
    val runes = listOf(rune1, rune2, rune3)

    val runify = remember<(String) -> String>(runesChar) {
        return@remember {
            it.map { runesChar.random() }.joinToString("")
        }
    }

    val gameMode by rememberPreference(
        AppPreferences::getGameModePreference,
        initialValue = AppPreferences.DEFAULT_GAME_MODE
    )

    val hintAvailable = remember(gameMode, attempts) {
        gameMode == GameMode.Easy || attempts.size >= 3
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val (isShowingHint, setIsShowingHint) = remember { mutableStateOf(false) }

    val hintLevel = remember(attempts.size) { (attempts.size - 2).coerceAtLeast(1) }
    val symbol = remember(hintLevel) { runes[(hintLevel - 1).coerceAtMost(runes.size - 1)] }

    
    val firstHint = remember(goal.date) {
        val similarItem = goal.solutionItems.groupBy { it.name }.maxBy { it.value.size }
        val similarCategory = goal.solutionItems.groupBy { it.category }.maxBy { it.value.size }

        if (similarItem.value.size > similarCategory.value.size)
            Pair(R.string.repeated_items, arrayOf(similarItem.value.size))
        else
            Pair(R.string.same_category_items, arrayOf(similarCategory.value.size))
    }

    val secondHint = remember(goal.date) {
        val indices = goal.solutionItems.indices
        val firstIndex = indices.random()
        val secondIndex = indices.filter { it != firstIndex }.random()

        val firstRevealed = goal.solutionItems[firstIndex].category
        val secondRevealed = goal.solutionItems[secondIndex].category

        if (firstRevealed == secondRevealed)
            Pair(R.string.revealed_category_2, arrayOf(firstRevealed))
        else
            Pair(R.string.revealed_category_1, arrayOf(firstRevealed, secondRevealed))
    }

    val lastHint = remember(goal.date) {
        val first = goal.solutionItems.first()
        val last = goal.solutionItems.last()

        if (goal.solutionItems.size > 2)
            Pair(R.string.revealed_items_2, arrayOf(first.category, last.category))
        else
            Pair(R.string.revealed_items_1, arrayOf(first.name))
    }

    if (hintAvailable) {
        Text(
            text = stringResource(R.string.hint_dots),
            fontFamily = Manrope,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    setIsShowingHint(true)
                }
                .size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                fontFamily = Manrope,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }

    if (isShowingHint) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .height(342.dp),
            sheetState = sheetState,
            onDismissRequest = {
                setIsShowingHint(false)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.runes_title),
                    fontFamily = Manrope,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rune1,
                        fontFamily = Manrope,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 10.dp, end = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                Color(251, 146, 60).opacity(0.25f),
                                RoundedCornerShape(12.dp)
                            )
                            .fillMaxWidth()
                            .padding(12.dp),
                    ) {
                        Text(
                            text = if (hintLevel < 1)
                                runify(stringResource(firstHint.first, formatArgs = firstHint.second))
                            else
                                stringResource(firstHint.first, formatArgs = firstHint.second),
                            fontFamily = Manrope,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .alpha(if (hintLevel < 2) 0.4f else 1f)
                                .blur(if (hintLevel < 1) 2.dp else 0.dp),
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rune2,
                        fontFamily = Manrope,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 10.dp, end = 16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .background(
                                Color(96, 165, 250).opacity(0.25f),
                                RoundedCornerShape(12.dp)
                            )
                            .fillMaxWidth()
                            .padding(12.dp),
                    ) {
                        Text(
                            text = if (hintLevel < 2)
                                runify(stringResource(secondHint.first, formatArgs = secondHint.second))
                            else
                                stringResource(secondHint.first, formatArgs = secondHint.second),
                            fontFamily = Manrope,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .alpha(if (hintLevel < 2) 0.4f else 1f)
                                .blur(if (hintLevel < 2) 2.dp else 0.dp),
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rune3,
                        fontFamily = Manrope,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 10.dp, end = 16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .background(
                                Color(167, 139, 250).opacity(0.25f),
                                RoundedCornerShape(12.dp)
                            )
                            .fillMaxWidth()
                            .padding(12.dp),
                    ) {
                        Text(
                            text = if (hintLevel < 3)
                                runify(stringResource(lastHint.first, formatArgs = lastHint.second))
                            else
                                stringResource(lastHint.first, formatArgs =lastHint.second),
                            fontFamily = Manrope,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .alpha(if (hintLevel < 3) 0.4f else 1f)
                                .blur(if (hintLevel < 3) 2.dp else 0.dp),
                        )
                    }
                }
            }
        }
    }
}