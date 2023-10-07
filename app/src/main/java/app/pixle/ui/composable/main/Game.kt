package app.pixle.ui.composable.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import app.pixle.database.AppPreferences
import app.pixle.lib.GameMode
import app.pixle.model.entity.attempt.Attempt
import app.pixle.model.entity.solution.Solution
import app.pixle.ui.composition.GameAnimation
import app.pixle.ui.composition.rememberGameAnimation
import app.pixle.ui.state.rememberPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Game(attempts: List<Attempt>, goal: Solution) {
    val scope = rememberCoroutineScope()

    val (animationState, setAnimationState) = rememberGameAnimation()
    val gameMode by rememberPreference(AppPreferences::getGameModePreference,
        initialValue = AppPreferences.DEFAULT_GAME_MODE
    )

    val isNotEndedYet = remember(gameMode, attempts) {
        // If endless, have at least 6 attempts, and none of them are winning attempts
        gameMode == GameMode.Easy && attempts.size >= 6 && attempts.all { !it.isWinningAttempt }
    }

    LaunchedEffect(animationState) {
        if (animationState == GameAnimation.State.IDLE) return@LaunchedEffect
        scope.launch {
            delay(100)
            setAnimationState(GameAnimation.State.IDLE)
        }
    }

    if (isNotEndedYet) {
        attempts
            .takeLast(attempts.size.coerceAtMost(5))
            .forEachIndexed { idx, it ->
                RowAttempt(
                    items = it,
                    shouldAnimate =
                    idx == attempts.size - 1 &&
                            animationState != GameAnimation.State.IDLE
                )
            }

        MissingRowAttempt(size = goal.solutionItems.size)
        return
    }

    attempts
        .takeLast(attempts.size.coerceAtMost(6))
        .forEachIndexed { idx, it ->
            RowAttempt(
                items = it,
                shouldAnimate =
                idx == attempts.size - 1 &&
                        animationState != GameAnimation.State.IDLE
            )
        }

    (0 until (6 - attempts.size).coerceAtLeast(0))
        .forEach { _ ->
            MissingRowAttempt(size = goal.solutionItems.size)
        }
}