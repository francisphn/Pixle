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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.database.AppPreferences
import app.pixle.lib.GameMode
import app.pixle.model.entity.attempt.Attempt
import app.pixle.model.entity.solution.Solution
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberPreference
import app.pixle.ui.theme.Manrope

val RUNES = "ᚠᚡᚢᚣᚤᚥᚦᚧᚨᚩᚪᚫᚬᚭᚮᚯᚰᚱᚲᚳᚴᚵᚶᚷᚸᚹᚺᚻᚼᚽᚾᚿᛀᛁᛂᛃᛄᛅᛆᛇᛈᛉᛊᛋᛌᛍᛎᛏᛐᛑᛒᛓᛔᛕᛖᛗᛘᛙᛚᛛᛜᛝᛞᛟᛧᛦᛥᛤᛣᛡᛠᛨᛩᛮᛯᛰ"
        .split("")

val RUNES_SYMBOL = listOf("ᛋ", "ᚥ", "ᚡ")
fun runify(text: String): String {
    return text.map { RUNES.random() }.joinToString("")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Hint(goal: Solution, attempts: List<Attempt>, color: Color) {
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
    val symbol = remember(hintLevel) { RUNES_SYMBOL[(hintLevel - 1).coerceAtMost(RUNES_SYMBOL.size - 1)] }

    val firstHint = remember(goal.date) {
        val similarItem = goal.solutionItems.groupBy { it.name }.maxBy { it.value.size }
        val similarCategory = goal.solutionItems.groupBy { it.category }.maxBy { it.value.size }

        if (similarItem.value.size > similarCategory.value.size)
            "${similarItem.value.size} repeated item(s)"
        else
            "${similarCategory.value.size} item(s) of the same category"
    }

    val secondHint = remember(goal.date) {
        val indices = goal.solutionItems.indices
        val firstIndex = indices.random()
        val secondIndex = indices.filter { it != firstIndex }.random()

        val firstRevealed = goal.solutionItems[firstIndex].category
        val secondRevealed = goal.solutionItems[secondIndex].category

        if (firstRevealed == secondRevealed)
            "2 \"${firstRevealed}\" items"
        else
            "1 \"${firstRevealed}\" item and 1 \"${secondRevealed}\" item"
    }

    val lastHint = remember(goal.date) {
        val first = goal.solutionItems.first()
        val last = goal.solutionItems.last()

        if (goal.solutionItems.size > 2)
            "The first is of \"${first.category}\" and the last is of \"${last.category}\""
        else
            "The first item is ${first.name}"
    }

    if (hintAvailable) {
        Text(
            text = " •",
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
                    text = "Revealed runes",
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
                        text = "ᛋ",
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
                            text = if (hintLevel < 1) runify(firstHint) else firstHint,
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
                        text = "ᚥ",
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
                            text = if (hintLevel < 2) runify(secondHint) else secondHint,
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
                        text = "ᚡ",
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
                            text = if (hintLevel < 3) runify(lastHint) else lastHint,
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