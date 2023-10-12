package app.pixle.ui.composable.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.pixle.R
import app.pixle.model.entity.attempt.Attempt
import app.pixle.ui.composable.PolaroidFrame
import app.pixle.ui.composition.GameAnimation
import app.pixle.ui.composition.rememberGameAnimation
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberSoundEffect
import coil.compose.AsyncImage
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun Celebration(
    attempts: List<Attempt>?,
    content: @Composable () -> Unit,
) {
    val animationState by rememberGameAnimation()
    val confettiSound = rememberSoundEffect(R.raw.confetti)
    val uri = remember(attempts) { attempts?.firstOrNull { it.isWinningAttempt }?.winningPhoto }
    val (party, setParty) = remember { mutableStateOf<Party?>(null) }
    val rotation = remember(party) { if (Math.random() < 0.5f) 1.5f else -1.5f }
    val animatedRotation = animateFloatAsState(
        targetValue = party?.let { rotation } ?: 0f,
        label = "rotation",
        animationSpec = tween(300, 200)
    )

    LaunchedEffect(animationState) {
        if (animationState != GameAnimation.State.WIN) return@LaunchedEffect
        setParty(
            Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                position = Position.Relative(0.5, 0.3),
                emitter = Emitter(duration = 500, TimeUnit.MILLISECONDS).max(100),
            )
        )
        confettiSound.start()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        content()
        Box(
            modifier = Modifier
                .zIndex(30f)
                .background(MaterialTheme.colorScheme.onBackground.opacity(party?.let { 0.5f }
                    ?: 0f))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            party?.let {
                KonfettiView(
                    modifier = Modifier
                        .zIndex(30f)
                        .fillMaxSize(),
                    parties = listOf(it),
                    updateListener = object : OnParticleSystemUpdateListener {
                        override fun onParticleSystemEnded(
                            system: PartySystem, activeSystems: Int
                        ) {
                            if (activeSystems == 0) {
                                setParty(null)
                                confettiSound.stop()
                            }
                        }
                    })
            }

            AnimatedVisibility(
                visible = party != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PolaroidFrame(
                    modifier = Modifier
                        .zIndex(40f)
                        .rotate(animatedRotation.value)
                ) {
                    AsyncImage(
                        model = uri,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(250.dp)
                            .height(250.dp),
                        contentDescription = null,
                        fallback = painterResource(R.drawable.image)
                    )
                }
            }
        }
    }
}